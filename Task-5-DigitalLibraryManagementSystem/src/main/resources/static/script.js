
const $ = id => document.getElementById(id);

let user = null;
let editId = null;

async function api(url, options = {}) {
    const response = await fetch(url, options);

    const text = await response.text();

    let data = null;

    if (text) {
        try {
            data = JSON.parse(text);
        } catch {
            data = text;
        }
    }

    if (!response.ok) {
        throw new Error(
            typeof data === "string"
                ? data
                : data?.message || "Request failed"
        );
    }

    return data;
}

const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
}[c]));

function isAdmin() {
    return user?.role === 'ADMIN';
}

// LOGIN
$('loginForm').onsubmit = async e => {
    e.preventDefault();

    try {
        user = await api('/login', {
            method: 'POST',
            body: JSON.stringify({
                username: $('username').value,
                password: $('password').value
            })
        });

        $('login').classList.add('hidden');
        $('app').classList.remove('hidden');
        $('who').textContent = user.name + ' · ' + user.role;

        document.querySelectorAll('.admin').forEach(x => {
            x.classList.toggle('hidden', !isAdmin());
        });

        await refresh();

    } catch (x) {
        alert(x.message);
    }
};

// LOGOUT
$('logout').onclick = () => {
    user = null;
    $('app').classList.add('hidden');
    $('login').classList.remove('hidden');
};

// NAVIGATION
document.querySelectorAll('nav button').forEach(b => {
    b.onclick = () => {
        document.querySelectorAll('.page').forEach(p =>
            p.classList.add('hidden')
        );

        $(b.dataset.page).classList.remove('hidden');
        $('heading').textContent = b.textContent;

        load(b.dataset.page);
    };
});

// REFRESH
async function refresh() {
    await Promise.all([
        books(),
        members(),
        loans(),
        bookings()
    ]);

    await contacts();
    await stats();
}

// LOAD PAGES
async function load(p) {
    try {
        if (p === 'books') await books();
        if (p === 'members') await members();
        if (p === 'loans') await loans();
        if (p === 'bookings') await bookings();
        if (p === 'contact') await contacts();

        await stats();

    } catch (e) {
        alert(e.message);
    }
}

// BOOKS
async function books() {
    const q = encodeURIComponent($('search').value || '');

    const a = await api('/books' + (q ? '?search=' + q : ''));

    $('bookList').innerHTML = a.map(b => `
        <article class="card">
            <b>${b.available ? 'Available' : 'Issued'}</b>

            <h3>${esc(b.title)}</h3>

            <p>${esc(b.author)} · ${esc(b.category || 'General')}</p>

            <p>ISBN: ${esc(b.isbn || '-')}</p>

            ${isAdmin() ? `
                <button onclick="editBook(${b.bid})">Edit</button>
                <button onclick="delBook(${b.bid})">Delete</button>

                ${b.available ? `
                    <button onclick="issue(${b.bid})">Issue</button>
                ` : ''}
            ` : `
                <button onclick="reserve(${b.bid})">Reserve</button>
            `}
        </article>
    `).join('') || '<p>No books found.</p>';
}

// SEARCH
$('search').oninput = () => {
    books().catch(e => alert(e.message));
};

// ADD BOOK
$('addToggle').onclick = () => {
    $('bookForm').classList.remove('hidden');

    editId = null;

    $('bookForm').reset();
    $('formTitle').textContent = 'Add book';
};

$('cancel').onclick = () => {
    $('bookForm').classList.add('hidden');
};

// SAVE BOOK
$('bookForm').onsubmit = async e => {
    e.preventDefault();

    try {
        const b = {
            title: $('title').value,
            author: $('author').value,
            isbn: $('isbn').value,
            category: $('category').value
        };

        await api('/books' + (editId ? '/' + editId : ''), {
            method: editId ? 'PUT' : 'POST',
            body: JSON.stringify(b)
        });

        $('bookForm').classList.add('hidden');

        await refresh();

    } catch (x) {
        alert(x.message);
    }
};

// EDIT BOOK
async function editBook(id) {
    const b = (await api('/books')).find(x => x.bid === id);

    if (!b) {
        alert('Book not found');
        return;
    }

    editId = id;

    $('title').value = b.title;
    $('author').value = b.author;
    $('isbn').value = b.isbn || '';
    $('category').value = b.category || '';

    $('formTitle').textContent = 'Edit book';

    $('bookForm').classList.remove('hidden');
}

// DELETE BOOK
async function delBook(id) {
    if (confirm('Delete book?')) {
        try {
            await api('/books/' + id, {
                method: 'DELETE'
            });

            await refresh();

        } catch (e) {
            alert(e.message);
        }
    }
}

// MEMBERS
async function members() {
    const a = await api('/members');

    $('memberList').innerHTML = a.map(m => `
        <article class="card">
            <b>${esc(m.name)}</b>

            <p>${esc(m.email)} · ${esc(m.phone || '')}</p>

            ${isAdmin() ? `
                <button onclick="delMember(${m.id})">Remove</button>
            ` : ''}
        </article>
    `).join('') || '<p>No members registered.</p>';
}

// ADD MEMBER
$('memberForm').onsubmit = async e => {
    e.preventDefault();

    try {
        await api('/members', {
            method: 'POST',
            body: JSON.stringify({
                name: $('mname').value,
                email: $('memail').value,
                phone: $('mphone').value
            })
        });

        e.target.reset();
        await refresh();

    } catch (e) {
        alert(e.message);
    }
};

// DELETE MEMBER
async function delMember(id) {
    if (confirm('Remove member?')) {
        try {
            await api('/members/' + id, {
                method: 'DELETE'
            });

            await refresh();

        } catch (e) {
            alert(e.message);
        }
    }
}

// MEMBER SELECTION
async function memberChoice() {
    const a = await api('/members');

    if (!a.length) {
        throw Error('Register a member first.');
    }

    const id = prompt(
        'Enter member ID:\n' +
        a.map(m => m.id + ' - ' + m.name).join('\n')
    );

    return id ? Number(id) : null;
}

// ISSUE BOOK
async function issue(bookId) {
    try {
        const memberId = await memberChoice();

        if (memberId) {
            await api('/loans', {
                method: 'POST',
                body: JSON.stringify({ bookId, memberId })
            });
        }

        await refresh();

    } catch (e) {
        alert(e.message);
    }
}

// RESERVE BOOK
async function reserve(bookId) {
    try {
        const memberId = await memberChoice();

        if (memberId) {
            await api('/bookings', {
                method: 'POST',
                body: JSON.stringify({ bookId, memberId })
            });

            alert('Reservation request submitted');
        }

        await refresh();

    } catch (e) {
        alert(e.message);
    }
}

// LOANS
async function loans() {
    const a = await api('/loans');

    $('loanList').innerHTML =
        '<h3>Issue / Return records</h3>' +

        (a.map(l => `
            <p>
                <b>${esc(l.bookTitle)}</b> — ${esc(l.memberName)}
                <br>
                Issued ${l.issueDate}
                · Due ${l.dueDate}
                · Fine ₹${l.fine}
                · ${l.returned ? 'Returned' : 'Active'}

                ${isAdmin() && !l.returned ? `
                    <button onclick="giveBack(${l.id})">Return</button>
                ` : ''}
            </p>
            <hr>
        `).join('') || 'No loan records');
}

// RETURN BOOK
async function giveBack(id) {
    try {
        await api('/loans/' + id + '/return', {
            method: 'POST'
        });

        await refresh();

    } catch (e) {
        alert(e.message);
    }
}

// BOOKINGS
async function bookings() {
    const a = await api('/bookings');

    $('bookingList').innerHTML =
        '<h3>Advance booking requests</h3>' +

        (a.map(b => `
            <p>
                ${esc(b.bookTitle)}
                · Member #${b.memberId}
                · ${b.bookingDate}
                · ${b.status}
            </p>
        `).join('') || 'No bookings');
}

// CONTACT FORM
$('contactForm').onsubmit = async e => {
    e.preventDefault();

    try {
        await api('/contacts', {
            method: 'POST',
            body: JSON.stringify({
                name: $('cname').value,
                email: $('cemail').value,
                message: $('msg').value
            })
        });

        e.target.reset();

        $('sent').textContent = 'Message sent successfully.';

        await contacts();

    } catch (e) {
        alert(e.message);
    }
};

// CONTACT INBOX
async function contacts() {
    if (!isAdmin()) return;

    const a = await api('/contacts');

    $('inbox').innerHTML =
        '<h3>Contact inbox</h3>' +

        a.map(c => `
            <p>
                <b>${esc(c.name)}</b>
                (${esc(c.email)})
                <br>
                ${esc(c.message)}
            </p>
            <hr>
        `).join('');
}

// DASHBOARD COUNTS
async function stats() {
    const [b, m, l, k] = await Promise.all([
        api('/books'),
        api('/members'),
        api('/loans'),
        api('/bookings')
    ]);

    $('bc').textContent = b.length;
    $('mc').textContent = m.length;
    $('lc').textContent = l.filter(x => !x.returned).length;
    $('kc').textContent = k.length;
}
