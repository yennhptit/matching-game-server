create database memory_game;
use memory_game;

CREATE TABLE `user`(
    ID int AUTO_INCREMENT PRIMARY KEY,
    `username` varchar(255) UNIQUE,
    `password` varchar(255),
    numberOfGame int DEFAULT 0, //so van da choi
    numberOfWin int DEFAULT 0,  //so van thang
    numberOfDraw int DEFAULT 0, //so van hoa
    IsOnline int DEFAULT 0,
    IsPlaying int DEFAULT 0,
    star int DEFAULT 0 // so sao
);
CREATE TABLE friend(
    ID_User1 int NOT NULL,
    ID_User2 int NOT NULL,
    FOREIGN KEY (ID_User1) REFERENCES `user`(ID),
    FOREIGN KEY (ID_User2) REFERENCES `user`(ID),
    CONSTRAINT PK_friend PRIMARY KEY (ID_User1,ID_User2)
);