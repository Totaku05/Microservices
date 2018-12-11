from .models import *
import json
import datetime

def json_default(value):
	if isinstance(value, datetime.date):
		return dict(year = value.year, month = value.month, day = value.day)
	elif isinstance(value, datetime.time):
		return dict(hour = value.hour, minute = value.minute, second = value.second)
	else:
		return value.__dict__

def jsonToUser(jsonObj):
	id_ = jsonObj['id']
	login_ = jsonObj['login']
	password_ = jsonObj['password']
	role_ = jsonObj['role']
	return User(id = id_, login = login_, password = password_, role = role_)
	
def jsonToContactInfo(jsonObj):
	id_ = jsonObj['id']
	firstName_ = jsonObj['firstName']
	secondName_ = jsonObj['secondName']
	phoneNumber_ = jsonObj['phoneNumber']
	email_ = jsonObj['email']
	return ContactInfo(id = id_, firstName = firstName_, secondName = secondName_, phoneNumber = phoneNumber_, email = email_)
	
def jsonToBlogger(jsonObj):
	id_ = jsonObj['id']
	account_ = jsonObj['account']
	card_number_ = jsonObj['card_number']
	minPrice_ = jsonObj['minPrice']
	countOfSubscribers_ = jsonObj['countOfSubscribers']
	status_ = jsonObj['status']
	login_ = jsonObj['login']
	return Blogger(id = id_, account = account_, card_number = card_number_, minPrice = minPrice_, countOfSubscribers = countOfSubscribers_, status = status_, login = login_)
	
def jsonToAdvertiser(jsonObj):
	id_ = jsonObj['id']
	account_ = jsonObj['account']
	card_number_ = jsonObj['card_number']
	login_ = jsonObj['login']
	return Advertiser(id = id_, account = account_, card_number = card_number_, login = login_)
	
def jsonToVideo(jsonObj):
	id_ = jsonObj['id']
	name_ = jsonObj['name']
	description_ = jsonObj['description']
	tag_ = jsonObj['tag']
	dateOfCreation_ = jsonObj['dateOfCreation']
	duration_ = jsonObj['duration']
	countOfLikes_ = jsonObj['countOfLikes']
	countOfDislikes_ = jsonObj['countOfDislikes']
	countOfViews_ = jsonObj['countOfViews']
	completed_order_ = jsonObj['completed_order']
	owner_ = jsonObj['owner']
	return Video(id = id_, name = name_, description = description_, tag = tag_, dateOfCreation = dateOfCreation_, duration = duration_, \
					countOfLikes = countOfLikes_, countOfDislikes = countOfDislikes_, countOfViews = countOfViews_, completed_order = completed_order_, \
					owner = owner_)
					
def jsonToOrder(jsonObj):
	id_ = jsonObj['id']
	name_ = jsonObj['name']
	description_ = jsonObj['description']
	tag_ = jsonObj['tag']
	sum_ = jsonObj['sum']
	startDate_ = jsonObj['startDate'].split('-')
	startDate_ = datetime.date(int(startDate_[0]), int(startDate_[1]), int(startDate_[2]))
	endDate_ = jsonObj['endDate']
	if(endDate_ != None):
		endDate_ = endDate_.split('-')
		endDate_ = datetime.date(int(endDate_[0]), int(endDate_[1]), int(endDate_[2]))
	lastUpdateDate_ = jsonObj['lastUpdateDate'].split('-')
	lastUpdateDate_ = datetime.date(int(lastUpdateDate_[0]), int(lastUpdateDate_[1]), int(lastUpdateDate_[2]))
	state_ = jsonObj['state']
	blogger_ = jsonObj['blogger']
	owner_ = jsonObj['owner']
	return Order(id = id_, name = name_, description = description_, tag = tag_, sum = sum_, startDate = startDate_, \
					endDate = endDate_, lastUpdateDate = lastUpdateDate_, state = state_, blogger = blogger_, \
					owner = owner_)