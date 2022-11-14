SELECT F.*, group_concat(FG.GENRE_ID separator ',') AS GENRE
FROM FILMS F
JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID WHERE id = 2
group by F.ID;