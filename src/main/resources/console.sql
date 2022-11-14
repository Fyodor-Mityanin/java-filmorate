SELECT F.*, group_concat(FG.GENRE_ID separator ',') AS GENRE
FROM FILMS F
JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID WHERE id = 2
group by F.ID;

SELECT U.*, group_concat(S.SUBSCRIBER separator ',') AS SUBCRIBERS
FROM USERS U
JOIN SUBSCRIBES S ON U.ID = S.AUTHOR WHERE U.ID = 2
GROUP BY U.ID;

SELECT U.*,
       group_concat(S.SUBSCRIBER separator ',') AS SUBCRIBERS
FROM USERS U
LEFT JOIN SUBSCRIBES S ON U.ID = S.AUTHOR
WHERE U.ID = 1
GROUP BY U.ID;

SELECT U.*
FROM SUBSCRIBES
JOIN USERS U on U.ID = SUBSCRIBES.SUBSCRIBER
WHERE AUTHOR=1