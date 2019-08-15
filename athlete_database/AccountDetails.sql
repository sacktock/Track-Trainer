BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS `AccountDetails` (
	`AccountID`	INTEGER,
	`FirstName`	TEXT,
	`LastName`	TEXT,
	`Email`	TEXT,
	`DateOfBirth`	TEXT,
	`Gender`	TEXT,
	`Username`	TEXT,
	`Password`	TEXT,
	`RaceDistance`	TEXT,
	PRIMARY KEY(`AccountID`)
);
INSERT INTO `AccountDetails` VALUES (1,'Alex','Goodall','alex.w.goodall@gmail.com','09/30/1999','Male','admin','password','800m');
COMMIT;
