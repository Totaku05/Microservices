class User:
	def __init__(self, id, login, password, role):
		self.id = id
		self.password = password
		self.role = role
		self.login = login
		self.info = id

class ContactInfo:
	def __init__(self, id, firstName, secondName, phoneNumber, email):
		self.id = id
		self.firstName = firstName
		self.secondName = secondName
		self.phoneNumber = phoneNumber
		self.email = email
	
class Advertiser:
	def __init__(self, id, account, card_number, login):
		self.id = id
		self.account = account
		self.card_number = card_number
		self.login = login
	
class Blogger:
	def __init__(self, id, account, card_number, minPrice, countOfSubscribers, status, login):
		self.id = id
		self.account = account
		self.card_number = card_number
		self.minPrice = minPrice
		self.countOfSubscribers = countOfSubscribers
		self.status = status
		self.login = login
	
class Order:
	def __init__(self, id, name, description, tag, sum, startDate, endDate, lastUpdateDate, state, blogger, owner):
		self.id = id
		self.name = name
		self.description = description
		self.tag = tag
		self.sum = sum
		self.startDate = startDate
		self.endDate = endDate
		self.lastUpdateDate = lastUpdateDate
		self.state = state
		self.blogger = blogger
		self.owner = owner
		
class Video:
	def __init__(self, id, name, description, tag, dateOfCreation, duration, countOfLikes, countOfDislikes, countOfViews, completed_order, owner):
		self.id = id
		self.name = name
		self.description = description
		self.tag = tag
		self.dateOfCreation = dateOfCreation
		self.duration = duration
		self.countOfLikes = countOfLikes
		self.countOfDislikes = countOfDislikes
		self.countOfViews = countOfViews
		self.completed_order = completed_order
		self.owner = owner
	