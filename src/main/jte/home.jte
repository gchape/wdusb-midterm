@import java.util.List

<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Library</title>
    <link rel="stylesheet" href="/style.css">
</head>
<body>

<nav>
    <a href="/" class="logo">Bibliotheca</a>
</nav>

<main>
    <section class="hero">
        <h1>Every great story lives here.</h1>
        <p>Discover, catalogue, and review books from a curated collection. Explore works by genre, find authors you
            love, and share your reading journey.</p>
        <div class="actions">
            <a href="/books" class="btn">Browse Library</a>
            <a href="/books/new" class="btn btn-secondary">Add a Book</a>
        </div>
    </section>

    <section class="search-section">
        <h2>Find your next read</h2>
        <form action="/books/search" method="get" class="search-form">
            <label>
                <input type="text" name="q" placeholder="e.g. The Great Gatsby, Fitzgerald...">
            </label>
            <button type="submit" class="btn">Search</button>
        </form>
    </section>

    <section class="features">
        <h2>Everything you need</h2>
        <div class="grid">
            <div class="card">
                <h3>📚 Book Catalogue</h3>
                <p>Manage your entire collection with titles, ISBNs, genres, page counts, and publication dates all in
                    one place.</p>
            </div>
            <div class="card">
                <h3>✍️ Author Profiles</h3>
                <p>Detailed author records with biographies, contact details, and a full listing of their published
                    works.</p>
            </div>
            <div class="card">
                <h3>⭐ Reviews & Ratings</h3>
                <p>Readers can leave 1–5-star ratings and written reviews, building a rich community of literary
                    opinion.</p>
            </div>
            <div class="card">
                <h3>🔍 Smart Search</h3>
                <p>Filter books by genre, author, rating, or publication date. Find exactly what you're looking for,
                    fast.</p>
            </div>
        </div>
    </section>

    <section class="recent">
        <h2>Recently Added</h2>
        <div class="grid">
            <div class="card book-card">
                <h3>The Midnight Library</h3>
                <p><strong>Matt Haig</strong><br><small>Fiction</small></p>
            </div>
            <div class="card book-card">
                <h3>Sapiens</h3>
                <p><strong>Yuval Noah Harari</strong><br><small>History</small></p>
            </div>
            <div class="card book-card">
                <h3>The Name of the Wind</h3>
                <p><strong>Patrick Rothfuss</strong><br><small>Fantasy</small></p>
            </div>
            <div class="card book-card">
                <h3>Crime and Punishment</h3>
                <p><strong>Fyodor Dostoevsky</strong><br><small>Classic</small></p>
            </div>
        </div>
    </section>
</main>

<footer>
    <p>Bibliotheca Book Library System &mdash; Built with JTE &amp; Kotlin</p>
</footer>

</body>
</html>