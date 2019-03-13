from django.shortcuts import render, redirect
from django.http import HttpResponse

from .models import *

import os
import json
import pytz
from datetime import datetime

import google.oauth2.credentials
import google_auth_oauthlib.flow
import googleapiclient.discovery

CLIENT_SECRETS_FILE = "client_secret.json"

SCOPES = ['https://www.googleapis.com/auth/youtube.force-ssl']
API_SERVICE_NAME = 'youtube'
API_VERSION = 'v3'

APP_NAME = 'data_collector'

def index(request):
	if not request.session.has_key('credentials'):
		return redirect('authorize/')
	
	credentials = google.oauth2.credentials.Credentials(**request.session['credentials'])
	client = googleapiclient.discovery.build(API_SERVICE_NAME, API_VERSION, credentials = credentials)
	
	find_data_for_education(client, 'new_queries.json')
	return HttpResponse('All queries are made.')
	
def authorize(request):
	os.environ['OAUTHLIB_INSECURE_TRANSPORT'] = '1'
	flow = google_auth_oauthlib.flow.Flow.from_client_secrets_file(CLIENT_SECRETS_FILE, scopes=SCOPES)
	flow.redirect_uri = request.build_absolute_uri('/' + APP_NAME + '/oauth2callback/')
	authorization_url, state = flow.authorization_url(access_type='offline', include_granted_scopes='true')
	request.session['state'] = state
	return redirect(authorization_url)
	
def oauth2callback(request):
	state = request.session['state']
	flow = google_auth_oauthlib.flow.Flow.from_client_secrets_file(CLIENT_SECRETS_FILE, scopes=SCOPES, state=state)
	flow.redirect_uri = request.build_absolute_uri('/' + APP_NAME + '/oauth2callback/')
	authorization_response = request.build_absolute_uri()
	flow.fetch_token(authorization_response = authorization_response)
	credentials = flow.credentials
	request.session['credentials'] = {
		'token': credentials.token,
		'refresh_token': credentials.refresh_token,
		'token_uri': credentials.token_uri,
		'client_id': credentials.client_id,
		'client_secret': credentials.client_secret,
		'scopes': credentials.scopes
	}
	return redirect(request.build_absolute_uri('/' + APP_NAME + '/'))

def find_data_for_education(client, file_name):
	queries = {}
	with open(file_name) as file:  
		json_file = json.load(file)
		for query in json_file['queries']:
			queries[query['query']] = query['count']
	for query, count in queries.items():
		find_videos_with_advertisement(client, query, count)
		#for video in Video.objects.filter(completedOrder = query):
		#	retrieve_other_videos_from_channel(client, video)

def get_video_from_response(client, video_item, completedOrder):
	id = video_item['id']
	title = video_item['snippet']['title']
	description = video_item['snippet']['description']
	publishedAt = video_item['snippet']['publishedAt']
	duration = video_item['contentDetails']['duration']
	viewCount = video_item['statistics'].get('viewCount', 0)
	likeCount = video_item['statistics'].get('likeCount', 0)
	dislikeCount = video_item['statistics'].get('dislikeCount', 0)
	completedOrder = completedOrder
	
	#Get category title
	'''category_response = client.videoCategories().list(
		part = 'snippet',
		id = video_item['snippet']['categoryId']
	).execute()'''
	category = 'None' #category_response['items'][0]['snippet']['title']
	
	#Get or create Blogger
	channelId = video_item['snippet']['channelId']
	try:
		blogger = Blogger.objects.get(id = channelId)
	except Blogger.DoesNotExist:
		channel_response = client.channels().list(
			part = 'snippet, statistics',
			id = channelId
		).execute()
		channel_info = channel_response['items'][0]
		blogger = Blogger(id = channel_info['id'], title = channel_info['snippet']['title'], \
				description = channel_info['snippet'].get('description', 'No description'), \
				publishedAt = channel_info['snippet']['publishedAt'], \
				subscriberCount = channel_info['statistics'].get('subscriberCount', 0), \
				videoCount = channel_info['statistics'].get('videoCount', 0))
		blogger.save()
				
	video = Video(id = id, blogger = blogger, title = title, description = description, \
			publishedAt = publishedAt, duration = duration, viewCount = viewCount, \
			likeCount = likeCount, dislikeCount = dislikeCount, category = category, \
			completedOrder = completedOrder)
	return video

def retrieve_other_videos_from_channel(client, video):
	maxResults = 25
	pageToken = None
	
	blogger = video.blogger
	for counter in range(0, blogger.videoCount, maxResults):
		response = client.search().list(
			channelId = blogger.id,
			type = 'video',
			part = 'id, snippet',
			maxResults = maxResults,
			order = 'date',
			pageToken = pageToken
		).execute()
		search_videos = []
		for item in response.get('items', []):
			item_publishedAt = datetime.strptime(item['snippet']['publishedAt'], '%Y-%m-%dT%H:%M:%S.%fZ')
			item_publishedAt = pytz.UTC.localize(item_publishedAt)
			if (item_publishedAt < video.publishedAt):
				search_videos.append(item['id']['videoId'])
		search_videos = ','.join(search_videos)
	
		video_response = client.videos().list(
			id = search_videos,
			part = 'snippet, statistics, contentDetails',
		).execute()
		for video_item in video_response.get('items', []):
			new_video = get_video_from_response(client, video_item, None)
			try:
				test = Video.objects.get(id = new_video.id)
			except Video.DoesNotExist:
				new_video.save();
		pageToken = response.get('nextPageToken', None)

def get_categories(client, regionCode):
	categories = {}
	response = client.videoCategories().list(
		part = 'snippet',
		regionCode = regionCode
	).execute()
	for item in response['items']:
		categories[item['id']] = item['snippet']['title']
	return categories
	
def find_videos_with_advertisement(client, q, count):
	maxResults = 25
	pageToken = None
	counter = 0
	
	#for counter in range(0, count, maxResults):
	while(counter < count):
		countOfVideos = 0
		response = client.search().list(
			q = q,
			type = 'video',
			part = 'id',
			maxResults = maxResults,
			order = 'viewCount',
			pageToken = pageToken
		).execute()
		search_videos = []
		for video_item in response.get('items', []):
			try:
				test = Video.objects.get(id = video_item['id']['videoId'])
			except Video.DoesNotExist:
				if counter + countOfVideos < count:
					search_videos.append(video_item['id']['videoId'])
					countOfVideos += 1
		if countOfVideos == 0:
			pageToken = response.get('nextPageToken', None)
			continue
		search_videos = ','.join(search_videos)
		
		video_response = client.videos().list(
			id = search_videos,
			part = 'snippet, statistics, contentDetails'
		).execute()
		for video_item in video_response.get('items', []):
			#Video information
			video = get_video_from_response(client, video_item, q)
			video.save()
		pageToken = response.get('nextPageToken', None)
		counter += countOfVideos