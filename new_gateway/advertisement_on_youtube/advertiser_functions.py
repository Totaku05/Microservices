from .models import *
from .json_functions import *
import requests
import datetime
import json

def create_order(user_id, form):
	name_ = form.cleaned_data['name']
	description_ = form.cleaned_data['description']
	tag_ = form.cleaned_data['tag']
	
	sum_ = form.cleaned_data['sum']
	if(sum_ <= 0):
		raise RuntimeError('Sum must be greater than zero.')
	
	startDate_ = datetime.date.today()
	lastUpdateDate_ = datetime.date.today()

	order = Order(id = -1, name = name_, description = description_, tag = tag_, sum = sum_, startDate = startDate_, \
					endDate = None, lastUpdateDate = lastUpdateDate_, state = 'InProgress', blogger = -1, \
					owner = user_id)
	order_data = json.dumps(order, default = lambda o: json_default(o))
	r = requests.post('http://localhost:8585/orders/order/', data = order_data)
	if(r.status_code != 201):
		raise RuntimeError(r.json()['errorMessage'])