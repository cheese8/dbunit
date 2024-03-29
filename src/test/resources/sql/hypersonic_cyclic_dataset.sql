CREATE TABLE A
(
    PKA CHAR(2),
    FKB CHAR(2),
    PRIMARY KEY (PKA)
);

CREATE TABLE B
(
    PKB CHAR(2),
    FKC CHAR(2),
    PRIMARY KEY (PKB)
);

CREATE TABLE C
(
    PKC CHAR(2),
    FKA CHAR(2),
    PRIMARY KEY (PKC)
);


INSERT INTO A VALUES ( 'A1', 'B1' );
INSERT INTO A VALUES ( 'A2', 'B1' );
INSERT INTO B VALUES ( 'B1', 'C1' );
INSERT INTO C VALUES ( 'C1', 'A2' );

ALTER TABLE A
    ADD CONSTRAINT AB FOREIGN KEY (FKB) REFERENCES B (PKB);
ALTER TABLE C
    ADD CONSTRAINT CA FOREIGN KEY (FKA) REFERENCES A (PKA);
ALTER TABLE B
    ADD CONSTRAINT BC FOREIGN KEY (FKC) REFERENCES C (PKC);
