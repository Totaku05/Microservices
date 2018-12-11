from .models import *
from .json_functions import *
import requests
import datetime
import json
import base64
					
def fill_sign_up_form(form, id):
	r = requests.get('http://localhost:9898/users/user/' + id)
	user = jsonToUser(r.json())
	contact_info = jsonToContactInfo(r.json()['contactInfo'])
	form.fields['firstName'].initial = contact_info.firstName
	form.fields['secondName'].initial = contact_info.secondName
	form.fields['phoneNumber'].initial = contact_info.phoneNumber
	form.fields['email'].initial = contact_info.email
	form.fields['login'].initial = user.login
	form.fields['password'].initial = base64.b64decode(user.password).decode()
	
	if(user.role == 'Blogger'):
		r = requests.get('http://localhost:9898/users/blogger/' + id)
		blogger = jsonToBlogger(r.json())
		form.fields['card_number'].initial = blogger.card_number
		form.fields['minPrice'].initial = blogger.minPrice
		form.fields['countOfSubscribers'].initial = blogger.countOfSubscribers
	else:
		r = requests.get('http://localhost:9898/users/advertiser/' + id)
		advertiser = jsonToAdvertiser(r.json())
		form.fields['card_number'].initial = advertiser.card_number

def fill_video_form(form, id):
	r = requests.get('http://localhost:9090/videos/video/' + id)
	video = jsonToVideo(r.json())
	form.fields['name'].initial = video.name
	form.fields['description'].initial = video.description
	form.fields['dateOfCreation'].initial = video.dateOfCreation
	form.fields['duration'].initial = video.duration
	form.fields['countOfLikes'].initial = video.countOfLikes
	form.fields['countOfDislikes'].initial = video.countOfDislikes
	form.fields['countOfViews'].initial = video.countOfViews
	form.fields['tag'].initial = video.tag
	
def get_account(id, role):
	if(role == 'Advertiser'):
		r = requests.get('http://localhost:9898/users/advertiser/' + id)
		advertiser = jsonToAdvertiser(r.json())
		account = advertiser.account
	else:
		r = requests.get('http://localhost:9898/users/blogger/' + id)
		blogger = jsonToBlogger(r.json())
		account = blogger.account
	return account

def update_user_account(form, id):
	r = requests.put('http://localhost:9898/users/user_account/' + id + '/' + str(form.cleaned_data['sum']) + '/' + 'false')
	if(r.status_code != 200):
		raise RuntimeError(r.json()['errorMessage'])

def get_orders(user_id, user_role):
	orders = []
	r = requests.get('http://localhost:9898/users/user/' + user_id)
	user = jsonToUser(r.json())
	
	if(user_role == 'Blogger'):
		r = requests.get('http://localhost:8585/orders/order_blogger/' + user_id)
		if(r.status_code == 204):
			return orders
			
		for elem in r.json():
			order = jsonToOrder(elem)
			if(order.endDate == None):
				order.endDate = 'Order isn\'t done.'
			order.blogger = user.login
			r = requests.get('http://localhost:9898/users/user/' + str(order.owner))
			order.advertiser = (jsonToUser(r.json())).login
			orders.append(order)
	else:
		r = requests.get('http://localhost:8585/orders/order_advertiser/' + user_id)
		if(r.status_code == 204):
			return orders
		
		for elem in r.json():
			order = jsonToOrder(elem)
			if(order.endDate == None):
				order.endDate = 'Order isn\'t done.'
			order.advertiser = user.login
			r = requests.get('http://localhost:9898/users/user/' + str(order.blogger))
			order.blogger = (jsonToUser(r.json())).login
			orders.append(order)
	return orders
	
def update_info(form, create, request):
	if(not create):
		r = requests.get('http://localhost:9898/users/user/' + str(request.session['user_id']))
		user = jsonToUser(r.json())
		contact_info = jsonToContactInfo(r.json()['contactInfo'])
		if(user.role == 'Blogger'):
			r = requests.get('http://localhost:9898/users/blogger/' + str(request.session['user_id']))
			blogger = jsonToBlogger(r.json())
		else:
			r = requests.get('http://localhost:9898/users/advertiser/' + str(request.session['user_id']))
			advertiser = jsonToAdvertiser(r.json())

	firstName_ = form.cleaned_data['firstName']
	secondName_ = form.cleaned_data['secondName']
	phoneNumber_ = form.cleaned_data['phoneNumber']
	email_ = form.cleaned_data['email']
	
	login_ = form.cleaned_data['login']
				
	password_ = base64.b64encode(form.cleaned_data['password'].encode()).decode()
	card_number_ = form.cleaned_data['card_number']
	
	if(create):
		user = User(id = -1, login = login_, password = password_, role = request.POST.get('role', ''))
		contact_info = ContactInfo(id = -1, firstName = firstName_, secondName = secondName_, phoneNumber = phoneNumber_, email = email_)
		user.contactInfo = contact_info
		user_data = '{user: ' + json.dumps(user, default = lambda o: json_default(o)) + '}'
		
		r = requests.post('http://localhost:9898/users/user/' + str(card_number_), data = user_data)
		if(r.status_code != 201):
			raise RuntimeError(r.json()['errorMessage'])
		user.id = r.json()['id']
		user.info = r.json()['info']
		user.contactInfo.id = r.json()['contactInfo']['id']
		id_ = r.json()['id']
	else:
		user.login = login_
		user.password = password_
		contact_info.firstName = firstName_
		contact_info.secondName = secondName_
		contact_info.phoneNumber = phoneNumber_
		contact_info.email = email_
		user.contactInfo = contact_info
		id_ = user.id
		
	request.session['user_id'] = id_
	request.session['user_role'] = request.POST.get('role', '')
	
	if(user.role == 'Blogger'):
		minPrice_ = request.POST.get('minPrice', '')
		if(create):
			status_ = 'Common'
			countOfSubscribers_ = request.POST.get('countOfSubscribers', '')
			blogger = Blogger(id = id_, account = 0, card_number = card_number_, minPrice = minPrice_, countOfSubscribers = countOfSubscribers_, status = status_, login = login_)
		else:
			blogger.card_number = card_number_
			blogger.minPrice = minPrice_
			blogger.countOfSubscribers = request.POST.get('countOfSubscribers', '')
			blogger.login = login_
		user_data = '{user: ' + json.dumps(user, default = lambda o: json_default(o)) + ', blogger: ' + \
								json.dumps(blogger, default = lambda o: json_default(o)) + '}'
	else:
		if(create):
			advertiser = Advertiser(id = id_, account = 0, card_number = card_number_, login = login_)
		else:
			advertiser.card_number = card_number_
			advertiser.login = login_
		user_data = '{user: ' + json.dumps(user, default = lambda o: json_default(o)) + ', advertiser: ' + \
								json.dumps(advertiser, default = lambda o: json_default(o)) + '}'
	
	r = requests.put('http://localhost:9898/users/user/' + str(id_), data = user_data)
	if(r.status_code != 200):
		raise RuntimeError(r.json()['errorMessage'])
		
def try_to_enter(form, request):
	login_ = form.cleaned_data['login']
	password_ = base64.b64encode(form.cleaned_data['password'].encode()).decode()
	
	r = requests.get('http://localhost:9898/users/user/' + login_ + '/' + password_)
	if(r.status_code != 200):
		raise RuntimeError("User with such login/password doesn't exist")
		
	request.session['user_id'] = r.json()['id']
	request.session['user_role'] = r.json()['role']