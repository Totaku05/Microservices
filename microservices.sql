CREATE DATABASE IF NOT EXISTS users;
CREATE DATABASE IF NOT EXISTS orders;
CREATE DATABASE IF NOT EXISTS videos;
CREATE DATABASE IF NOT EXISTS advertisement;

CREATE TABLE IF NOT EXISTS users.contact_info 
(id int PRIMARY KEY auto_increment,
 firstName varchar(50) not null,
 secondName varchar(50) not null,
 phoneNumber varchar(20) not null unique,
 email varchar(50) not null unique);

CREATE TABLE IF NOT EXISTS users.user 
(id int PRIMARY KEY auto_increment,
 password varchar(50) not null,
 role varchar(20) not null,
 login varchar(50) not null unique,
 info int references contact_info);

CREATE TABLE IF NOT EXISTS users.advertiser 
(id int PRIMARY KEY,
 account double not null,
 card_number int not null unique,
 login varchar(50) not null unique);
 
 CREATE TABLE IF NOT EXISTS users.blogger 
(id int PRIMARY KEY,
 account double not null,
 card_number int not null unique,
 minPrice double not null,
 countOfSubscribers int not null,
 status varchar(20) not null,
 login varchar(50) not null unique);
 
CREATE TABLE IF NOT EXISTS orders.orders 
(id int PRIMARY KEY auto_increment,
 name varchar(50) not null,
 description varchar(500) not null,
 tag varchar(20) not null,
 sum double not null,
 startDate date not null,
 endDate date,
 lastUpdateDate date not null,
 state varchar(20) not null,
 blogger int not null,
 owner int not null);
 
 CREATE TABLE IF NOT EXISTS videos.video 
(id int PRIMARY KEY auto_increment,
 name varchar(50) not null,
 description varchar(500) not null,
 tag varchar(20) not null,
 dateOfCreation date not null,
 duration time not null,
 countOfLikes int not null,
 countOfDislikes int not null,
 countOfViews int not null,
 completed_order int,
 owner int not null);
 
 CREATE INDEX user_index ON users.user (login);
 CREATE INDEX info_index ON users.contact_info (id);
 CREATE INDEX advertiser_index ON users.advertiser (id);
 CREATE INDEX blogger_index ON users.blogger (id);
 CREATE INDEX order_index ON orders.orders (owner);
 CREATE INDEX video_index ON videos.video (owner);
 
select * from users.contact_info;

INSERT INTO users.contact_info (firstName, secondName, phoneNumber, email) VALUES("Василий", "Пушкин", "+79040000000", "vasiliy_pushkin@gmail.com");
INSERT INTO users.contact_info (firstName, secondName, phoneNumber, email) VALUES("Степан", "Степанов", "+79040000001", "stepan_stepanov@gmail.com");
INSERT INTO users.contact_info (firstName, secondName, phoneNumber, email) VALUES("Федор", "Бондарчук", "+79040000002", "fedor_bondarchuk@gmail.com");
INSERT INTO users.contact_info (firstName, secondName, phoneNumber, email) VALUES("Матвей", "Гнездо", "+79040000003", "matvey_gnezdo@gmail.com");
INSERT INTO users.contact_info (firstName, secondName, phoneNumber, email) VALUES("Глеб", "Гаврилов", "+79040000004", "gleb_gavrilov@gmail.com");
INSERT INTO users.contact_info (firstName, secondName, phoneNumber, email) VALUES("Акакий", "Иванов", "+79040000005", "akakiy_ivanov@gmail.com");
INSERT INTO users.contact_info (firstName, secondName, phoneNumber, email) VALUES("Александра", "Плешко", "+79040000006", "alexandra_pleshko@gmail.com");
INSERT INTO users.contact_info (firstName, secondName, phoneNumber, email) VALUES("Мария", "Степанидзе", "+79040000007", "maria_stepanidze@gmail.com");
INSERT INTO users.contact_info (firstName, secondName, phoneNumber, email) VALUES("Фекла", "Писарчук", "+79040000008", "fekla_pisarchuk@gmail.com");
INSERT INTO users.contact_info (firstName, secondName, phoneNumber, email) VALUES("Валерия", "Соколова", "+79040000009", "valeria_sokolova@gmail.com");

select * from users.user;

INSERT INTO users.user (password, role, login, info) VALUES("MTIzNDU2", "Advertiser", "Horizon", 1);
INSERT INTO users.user (password, role, login, info) VALUES("MTIzNDU2", "Advertiser", "Alex", 2);
INSERT INTO users.user (password, role, login, info) VALUES("MTIzNDU2", "Advertiser", "Philips", 3);
INSERT INTO users.user (password, role, login, info) VALUES("MTIzNDU2", "Advertiser", "Cuckoo", 4);
INSERT INTO users.user (password, role, login, info) VALUES("MTIzNDU2", "Advertiser", "Altair", 5);
INSERT INTO users.user (password, role, login, info) VALUES("MTIzNDU2", "Blogger", "Wind", 6);
INSERT INTO users.user (password, role, login, info) VALUES("MTIzNDU2", "Blogger", "Raven", 7);
INSERT INTO users.user (password, role, login, info) VALUES("MTIzNDU2", "Blogger", "Mountain", 8);
INSERT INTO users.user (password, role, login, info) VALUES("MTIzNDU2", "Blogger", "Sickle", 9);
INSERT INTO users.user (password, role, login, info) VALUES("MTIzNDU2", "Blogger", "Falcon", 10);

select * from users.advertiser;

INSERT INTO users.advertiser (id, account, card_number, login) VALUES(1, 10000, 123456789, "Horizon");
INSERT INTO users.advertiser (id, account, card_number, login) VALUES(2, 10000, 123456788, "Alex");
INSERT INTO users.advertiser (id, account, card_number, login) VALUES(3, 10000, 123456787, "Philips");
INSERT INTO users.advertiser (id, account, card_number, login) VALUES(4, 10000, 123456786, "Cuckoo");
INSERT INTO users.advertiser (id, account, card_number, login) VALUES(5, 10000, 123456785, "Altair");

select * from users.blogger;

INSERT INTO users.blogger (id, account, card_number, minPrice, countOfSubscribers, status, login) VALUES(6, 10000, 123456779, 0, 10000, "Diamond", "Wind");
INSERT INTO users.blogger (id, account, card_number, minPrice, countOfSubscribers, status, login) VALUES(7, 10000, 123456769, 0, 10000, "Gold", "Raven");
INSERT INTO users.blogger (id, account, card_number, minPrice, countOfSubscribers, status, login) VALUES(8, 10000, 123456759, 0, 10000, "Silver", "Mountain");
INSERT INTO users.blogger (id, account, card_number, minPrice, countOfSubscribers, status, login) VALUES(9, 10000, 123456749, 0, 10000, "Bronze", "Sickle");
INSERT INTO users.blogger (id, account, card_number, minPrice, countOfSubscribers, status, login) VALUES(10, 10000, 123456739, 0, 10000, "Common", "Falcon");

select * from orders.orders;

INSERT INTO orders.orders (name, description, tag, sum, startDate, lastUpdateDate, state, blogger, owner) VALUES("First", "First order", "Food", 50, CURDATE(), CURDATE(), "InProgress", 6, 1);
INSERT INTO orders.orders (name, description, tag, sum, startDate, lastUpdateDate, state, blogger, owner) VALUES("Second", "Second order", "Tourism", 50, CURDATE(), CURDATE(), "InProgress", 7, 2);
INSERT INTO orders.orders (name, description, tag, sum, startDate, lastUpdateDate, state, blogger, owner) VALUES("Third", "Third order", "Electronics", 50, CURDATE(), CURDATE(), "InProgress", 8, 3);
INSERT INTO orders.orders (name, description, tag, sum, startDate, lastUpdateDate, state, blogger, owner) VALUES("Fourth", "Fourth order", "Other", 50, CURDATE(), CURDATE(), "InProgress", 9, 4);
INSERT INTO orders.orders (name, description, tag, sum, startDate, lastUpdateDate, state, blogger, owner) VALUES("Fifth", "Fifth order", "Other", 50, CURDATE(), CURDATE(), "InProgress", 10, 5);

select * from videos.video;

INSERT INTO videos.video (name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, owner) VALUES("MyFirstVideo", "MyFirstVideo!!!", "Food", CURDATE(), "001000", 1500, 0, 15000, 6);
INSERT INTO videos.video (name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, owner) VALUES("MyFirstVideo", "MyFirstVideo!!!", "Tourism", CURDATE(), "001500", 1000, 500, 15000, 7);
INSERT INTO videos.video (name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, owner) VALUES("MyFirstVideo", "MyFirstVideo!!!", "Electronics", CURDATE(), "001200", 750, 50, 20000, 8);
INSERT INTO videos.video (name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, owner) VALUES("MyFirstVideo", "MyFirstVideo!!!", "Other", CURDATE(), "001300", 100, 0, 5000, 9);
INSERT INTO videos.video (name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, owner) VALUES("MyFirstVideo", "MyFirstVideo!!!", "Other", CURDATE(), "001100", 700, 500, 25000, 10);
