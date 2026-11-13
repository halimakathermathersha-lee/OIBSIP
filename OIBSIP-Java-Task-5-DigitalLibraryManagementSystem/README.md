# Digital Library Management System — Oasis Infobyte Task 5

## Run
- Install Java 17+ and Maven.
- Open this folder in IntelliJ IDEA.
- Run `DigitalLibraryApplication.java`.
- Open http://localhost:8087

The app uses a local H2 file database; MySQL is not required.

## Demo accounts
- Admin: `admin` / `admin123`
- User: `user` / `user123`

## Included
- Admin and user demo login
- Book add/edit/delete and title/author search
- Member registration/removal
- Issue and return with 14-day due date
- Overdue fine calculation at ₹5/day
- Advance booking request records
- Contact form and admin inbox
- Dashboard summary

## Important limitations
This is a beginner demo, not production authentication. Demo credentials are hard-coded and backend endpoints are not protected by role authorization. Do not use real personal data. Advance booking records a request but does not automatically allocate a book. Fine is calculated, not paid through the app.
