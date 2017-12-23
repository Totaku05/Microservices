CREATE DATABASE IF NOT EXISTS identification;
CREATE DATABASE IF NOT EXISTS orders;
CREATE DATABASE IF NOT EXISTS bloggers;

CREATE TABLE IF NOT EXISTS identification.contact_info 
(id int PRIMARY KEY auto_increment,
 firstName varchar(50) not null,
 secondName varchar(50) not null,
 phoneNumber varchar(20) not null unique,
 email varchar(50) not null unique);

CREATE TABLE IF NOT EXISTS identification.user 
(id int PRIMARY KEY auto_increment,
 password varchar(50) not null,
 role varchar(20) not null,
 login varchar(50) not null unique,
 info int references contact_info);

CREATE TABLE IF NOT EXISTS orders.advertiser 
(id int PRIMARY KEY,
 account double not null,
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
 owner int references advertiser);
 
CREATE TABLE IF NOT EXISTS bloggers.blogger 
(id int PRIMARY KEY,
 account double not null,
 minPrice double not null,
 countOfSubscribers int not null,
 status varchar(20) not null,
 login varchar(50) not null unique);
 
 CREATE TABLE IF NOT EXISTS bloggers.video 
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
 owner int references blogger);
 
 CREATE INDEX user_index ON identification.user (login);
 CREATE INDEX info_index ON identification.contact_info (id);
 CREATE INDEX advertiser_index ON orders.advertiser (id);
 CREATE INDEX order_index ON orders.orders (owner);
 CREATE INDEX blogger_index ON bloggers.blogger (id);
 CREATE INDEX video_index ON bloggers.video (owner);
 
select * from identification.contact_info;

INSERT INTO identification.contact_info (firstName, secondName, phoneNumber, email) VALUES("Василий", "Пушкин", "+79040000000", "vasiliy_pushkin@gmail.com");
INSERT INTO identification.contact_info (firstName, secondName, phoneNumber, email) VALUES("Степан", "Степанов", "+79040000001", "stepan_stepanov@gmail.com");
INSERT INTO identification.contact_info (firstName, secondName, phoneNumber, email) VALUES("Федор", "Бондарчук", "+79040000002", "fedor_bondarchuk@gmail.com");
INSERT INTO identification.contact_info (firstName, secondName, phoneNumber, email) VALUES("Матвей", "Гнездо", "+79040000003", "matvey_gnezdo@gmail.com");
INSERT INTO identification.contact_info (firstName, secondName, phoneNumber, email) VALUES("Глеб", "Гаврилов", "+79040000004", "gleb_gavrilov@gmail.com");
INSERT INTO identification.contact_info (firstName, secondName, phoneNumber, email) VALUES("Акакий", "Иванов", "+79040000005", "akakiy_ivanov@gmail.com");
INSERT INTO identification.contact_info (firstName, secondName, phoneNumber, email) VALUES("Александра", "Плешко", "+79040000006", "alexandra_pleshko@gmail.com");
INSERT INTO identification.contact_info (firstName, secondName, phoneNumber, email) VALUES("Мария", "Степанидзе", "+79040000007", "maria_stepanidze@gmail.com");
INSERT INTO identification.contact_info (firstName, secondName, phoneNumber, email) VALUES("Фекла", "Писарчук", "+79040000008", "fekla_pisarchuk@gmail.com");
INSERT INTO identification.contact_info (firstName, secondName, phoneNumber, email) VALUES("Валерия", "Соколова", "+79040000009", "valeria_sokolova@gmail.com");

select * from identification.user;

INSERT INTO identification.user (password, role, login, info) VALUES("123456", "Advertiser", "Horizon", 1);
INSERT INTO identification.user (password, role, login, info) VALUES("123456", "Advertiser", "Alex", 2);
INSERT INTO identification.user (password, role, login, info) VALUES("123456", "Advertiser", "Philips", 3);
INSERT INTO identification.user (password, role, login, info) VALUES("123456", "Advertiser", "Cuckoo", 4);
INSERT INTO identification.user (password, role, login, info) VALUES("123456", "Advertiser", "Altair", 5);
INSERT INTO identification.user (password, role, login, info) VALUES("123456", "Blogger", "Wind", 6);
INSERT INTO identification.user (password, role, login, info) VALUES("123456", "Blogger", "Raven", 7);
INSERT INTO identification.user (password, role, login, info) VALUES("123456", "Blogger", "Mountain", 8);
INSERT INTO identification.user (password, role, login, info) VALUES("123456", "Blogger", "Sickle", 9);
INSERT INTO identification.user (password, role, login, info) VALUES("123456", "Blogger", "Falcon", 10);

select * from orders.advertiser;

INSERT INTO orders.advertiser (id, account, login) VALUES(1, 100, "Horizon");
INSERT INTO orders.advertiser (id, account, login) VALUES(2, 100, "Alex");
INSERT INTO orders.advertiser (id, account, login) VALUES(3, 100, "Philips");
INSERT INTO orders.advertiser (id, account, login) VALUES(4, 100, "Cuckoo");
INSERT INTO orders.advertiser (id, account, login) VALUES(5, 100, "Altair");

select * from orders.orders;

INSERT INTO orders.orders (name, description, tag, sum, startDate, lastUpdateDate, state, blogger, owner) VALUES("First", "First order", "Food", 50, CURDATE(), CURDATE(), "InProgress", 1, 1);
INSERT INTO orders.orders (name, description, tag, sum, startDate, lastUpdateDate, state, blogger, owner) VALUES("Second", "Second order", "Tourism", 50, CURDATE(), CURDATE(), "InProgress", 2, 2);
INSERT INTO orders.orders (name, description, tag, sum, startDate, lastUpdateDate, state, blogger, owner) VALUES("Third", "Third order", "Electronics", 50, CURDATE(), CURDATE(), "InProgress", 3, 3);
INSERT INTO orders.orders (name, description, tag, sum, startDate, lastUpdateDate, state, blogger, owner) VALUES("Fourth", "Fourth order", "Other", 50, CURDATE(), CURDATE(), "InProgress", 4, 4);
INSERT INTO orders.orders (name, description, tag, sum, startDate, lastUpdateDate, state, blogger, owner) VALUES("Fifth", "Fifth order", "Other", 50, CURDATE(), CURDATE(), "InProgress", 5, 5);

select * from bloggers.blogger;

INSERT INTO bloggers.blogger (id, account, minPrice, countOfSubscribers, status, login) VALUES(6, 100, 0, 10000, "Diamond", "Wind");
INSERT INTO bloggers.blogger (id, account, minPrice, countOfSubscribers, status, login) VALUES(7, 100, 0, 10000, "Gold", "Raven");
INSERT INTO bloggers.blogger (id, account, minPrice, countOfSubscribers, status, login) VALUES(8, 100, 0, 10000, "Silver", "Mountain");
INSERT INTO bloggers.blogger (id, account, minPrice, countOfSubscribers, status, login) VALUES(9, 100, 0, 10000, "Bronze", "Sickle");
INSERT INTO bloggers.blogger (id, account, minPrice, countOfSubscribers, status, login) VALUES(10, 100, 0, 10000, "Common", "Falcon");

select * from bloggers.video;

INSERT INTO bloggers.video (name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, owner) VALUES("MyFirstVideo", "MyFirstVideo!!!", "Food", CURDATE(), "001000", 1500, 0, 15000, 6);
INSERT INTO bloggers.video (name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, owner) VALUES("MyFirstVideo", "MyFirstVideo!!!", "Tourism", CURDATE(), "001500", 1000, 500, 15000, 7);
INSERT INTO bloggers.video (name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, owner) VALUES("MyFirstVideo", "MyFirstVideo!!!", "Electronics", CURDATE(), "001200", 750, 50, 20000, 8);
INSERT INTO bloggers.video (name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, owner) VALUES("MyFirstVideo", "MyFirstVideo!!!", "Other", CURDATE(), "001300", 100, 0, 5000, 9);
INSERT INTO bloggers.video (name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, owner) VALUES("MyFirstVideo", "MyFirstVideo!!!", "Other", CURDATE(), "001100", 700, 500, 25000, 10);
