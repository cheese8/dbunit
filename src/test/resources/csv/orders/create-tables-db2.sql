DROP TABLE ORDERS;
DROP TABLE ORDERS_ROW;

CREATE TABLE ORDERS
(
    ID          INTEGER,
    DESCRIPTION VARCHAR(50)
);
CREATE TABLE ORDERS_ROW
(
    ID          INTEGER,
    DESCRIPTION VARCHAR(50),
    QUANTITY    INTEGER
);