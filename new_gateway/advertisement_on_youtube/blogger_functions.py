from .models import *
from .json_functions import *
import requests
import datetime
import json

def getOrdersForNewVideo(id):
	r = requests.get('http://localhost:8585/orders/order_blogger/' + id)
	if(r.status_code == 204):
		return []
		
	orders = []
	for elem in r.json():
		order = jsonToOrder(elem)
		if(order.state != 'Done'):
			orders.append(order)
	return orders
	
def get_status(id):
	r = requests.get('http://localhost:9898/users/blogger/' + id)
	return r.json()['status']
	
def get_statuses():
	r = requests.get('http://localhost:9898/users/statuses/')
	statuses = []
	for elem in r.json():
		statuses.append(elem['key'] + ' ' + str(elem['value']))
	return statuses
	
def update_blogger_status(status, id):
	r = requests.put('http://localhost:9898/users/status/' + id + '/' + status)
	if(r.status_code != 200):
		raise RuntimeError(r.json()['errorMessage'])
		
def get_videos(id):
	r = requests.get('http://localhost:9090/videos/video_blogger/' + id)
	if(r.status_code == 204):
		return []
	
	videos = []
	for elem in r.json():
		video = jsonToVideo(elem)
		if(video.completed_order != None):
			r = requests.get('http://localhost:8585/orders/order/' + str(video.completed_order))
			order = jsonToOrder(r.json())
			video.nameOfOrder = order.name
		else:
			video.nameOfOrder = 'This video has no relations with orders'
		videos.append(video)
	return videos
		
def update_video_info(form, create, request):
	if(not create):
		r = requests.get('http://localhost:9090/videos/video/' + str(request.session['video_id']))
		video = jsonToVideo(r.json())

	name_ = form.cleaned_data['name']
	description_ = form.cleaned_data['description']
	dateOfCreation_ = form.cleaned_data['dateOfCreation']
	duration_ = form.cleaned_data['duration']
	countOfLikes_ = form.cleaned_data['countOfLikes']
	countOfDislikes_ = form.cleaned_data['countOfDislikes']
	countOfViews_ = form.cleaned_data['countOfViews']
	tag_ = form.cleaned_data['tag']
	
	if(create):
		completed_order_ = request.POST.get('completed_order', '')
		if(completed_order_ == '' or completed_order_ == '-1'):
			completed_order_ = None
		else:
			r = requests.get('http://localhost:8585/orders/order/' + completed_order_)
			if(r.status_code != 200):
				raise RuntimeError(r.json()['errorMessage'])	
			order = jsonToOrder(r.json())
			order.state = 'Done'
			order.endDate = datetime.date.today()
			order.lastUpdateDate = datetime.date.today()
			order_data = json.dumps(order, default = lambda o: json_default(o))
			r = requests.put('http://localhost:8585/orders/order/status/' + completed_order_, data = order_data)
			if(r.status_code != 200):
				raise RuntimeError(r.json()['errorMessage'])
			completed_order_ = int(completed_order_)
			
		video = Video(id = -1, name = name_, description = description_, tag = tag_, dateOfCreation = dateOfCreation_, duration = duration_, \
					countOfLikes = countOfLikes_, countOfDislikes = countOfDislikes_, countOfViews = countOfViews_, completed_order = completed_order_, \
					owner = request.session['user_id'])
		video_data = json.dumps(video, default = lambda o: json_default(o))
		
		r = requests.post('http://localhost:9090/videos/video/', data = video_data)
		if(r.status_code != 201):
			raise RuntimeError(r.json()['errorMessage'])
	else:
		video.name = name_
		video.description = description_
		video.tag = tag_
		video.dateOfCreation = dateOfCreation_
		video.duration = duration_
		video.countOfLikes = countOfLikes_
		video.countOfDislikes = countOfDislikes_
		video.countOfViews = countOfViews_
		video_data = json.dumps(video, default = lambda o: json_default(o))
		r = requests.put('http://localhost:9090/videos/video/' + str(video.id), data = video_data)
		if(r.status_code != 200):
			raise RuntimeError(r.json()['errorMessage'])
			
