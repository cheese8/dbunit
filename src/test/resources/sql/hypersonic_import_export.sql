/**

 Create tables used to test ImportedAndExportedKeysSearchCallback's ordering algorithm with 
 the following dependencies:

    A - B
    |
    C    D - E
          \ /
           F  
	     
 
*/

CREATE TABLE A
(
    PKA NUMERIC,
    FKB NUMERIC,
    PRIMARY KEY (PKA)
);

CREATE TABLE B
(
    PKB NUMERIC,
    PRIMARY KEY (PKB)
);

CREATE TABLE C
(
    PKC NUMERIC,
    FKA NUMERIC,
    PRIMARY KEY (PKC)
);

CREATE TABLE D
(
    PKD NUMERIC,
    FKE NUMERIC,
    FKF NUMERIC,
    PRIMARY KEY (PKD)
);

CREATE TABLE E
(
    PKE NUMERIC,
    FKF NUMERIC,
    PRIMARY KEY (PKE)
);

CREATE TABLE F
(
    PKF NUMERIC,
    PRIMARY KEY (PKF)
);

ALTER TABLE A
    ADD CONSTRAINT AB FOREIGN KEY (FKB) REFERENCES B (PKB);

ALTER TABLE C
    ADD CONSTRAINT CA FOREIGN KEY (FKA) REFERENCES A (PKA);

ALTER TABLE D
    ADD CONSTRAINT DE FOREIGN KEY (FKE) REFERENCES E (PKE);
ALTER TABLE D
    ADD CONSTRAINT DF FOREIGN KEY (FKF) REFERENCES F (PKF);

ALTER TABLE E
    ADD CONSTRAINT EF FOREIGN KEY (FKF) REFERENCES F (PKF);
