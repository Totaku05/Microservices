from django.shortcuts import render
from django.http import HttpResponse
from django.shortcuts import redirect
from .forms import *
from .common_functions import *
from .blogger_functions import *
from .advertiser_functions import *
	
def sign_in(request):
	if request.method == 'POST':
		form = SignInForm(request.POST)
		if form.is_valid():
			try:
				try_to_enter(form, request)
			except RuntimeError as e:
				context = {'form': form, 'error_message': str(e)}
				return render(request, 'index.html', context)
				
			return redirect('/adv/orders/')
	else:
		form = SignInForm()
	return render(request, 'index.html', {'form': form})
	
def sign_up(request):
	if request.method == 'POST':
		form = SignUpForm(request.POST)
		if form.is_valid():
			try:
				update_info(form, True, request)
			except RuntimeError as e:
				context = {'form': form, 'error_message': str(e)}
				return render(request, 'sign_up.html', context)
			
			return redirect('/adv/orders/')
	else:
		role_ = request.GET.get('role', '')
		form = SignUpForm()
		form.add_fields(role_)
	return render(request, 'sign_up.html', {'form': form})
	
def change_user_info(request):
	if(not request.session.has_key('user_id')):
		return redirect('/adv/')
	if request.method == 'POST':
		form = SignUpForm(request.POST)
		if form.is_valid():
			try:
				update_info(form, False, request)
			except RuntimeError as e:
				context = {'form': form, 'error_message': str(e), 'role': request.session['user_role']}
				return render(request, 'change_user_info.html', context)
				
			return redirect('/adv/orders/')
	else:
		form = SignUpForm()
		form.add_fields(request.session['user_role'])
		fill_sign_up_form(form, str(request.session['user_id']))
	return render(request, 'change_user_info.html', {'form': form, 'role': request.session['user_role']})
	
def update_account(request):
	if(not request.session.has_key('user_id')):
		return redirect('/adv/')
	id = str(request.session['user_id'])
	role = request.session['user_role']
	if request.method == 'POST':
		form = UpdateAccountForm(request.POST)
		if form.is_valid():
			try:
				update_user_account(form, id)
			except RuntimeError as e:
				account = get_account(id, role)
				context = {'form': form, 'error_message': str(e), 'account': account, 'role': role}
				return render(request, 'update_account.html', context)
				
			return redirect('/adv/change_user_info/')
	else:
		form = UpdateAccountForm()
		account = get_account(id, role)
		return render(request, 'update_account.html', {'form': form, 'account': account, 'role': role})

def update_status(request):
	if(not request.session.has_key('user_id')):
		return redirect('/adv/')
	if(request.session['user_role'] != 'Blogger'):
		return redirect('/adv/orders/')
	id = str(request.session['user_id'])
	role = request.session['user_role']
	if request.method == 'POST':
		form = UpdateStatusForm(request.POST)
		if form.is_valid():
			try:
				update_blogger_status(str(request.POST.get('status', '')), id)
			except RuntimeError as e:
				account = get_account(id, role)
				statuses = get_statuses()
				context = {'form': form, 'error_message': str(e), 'account': account, 'statuses': statuses, 'role': role}
				return render(request, 'update_status.html', context)
				
			return redirect('/adv/change_user_info/')
	else:
		status = get_status(id)
		if(status == 'Diamond'):
			context = {'diamond': True, 'role': role}
		else:
			form = UpdateStatusForm()
			form.add_status_field(status)
			account = get_account(id, role)
			statuses = get_statuses()
			context = {'form': form, 'account': account, 'statuses': statuses, 'role': role}
		return render(request, 'update_status.html', context)

def videos(request):
	if(not request.session.has_key('user_id')):
		return redirect('/adv/')
	if(request.session['user_role'] != 'Blogger'):
		return redirect('/adv/orders/')
	videos_ = get_videos(str(request.session['user_id']))
	context = {'role': request.session['user_role']}
	if(len(videos_) == 0):
		context['error_message'] = "You have no videos"
	else:
		context['videos'] = videos_
	return render(request, 'videos.html', context)
		
def new_video(request):
	if(not request.session.has_key('user_id')):
		return redirect('/adv/')
	if(request.session['user_role'] != 'Blogger'):
		return redirect('/adv/orders/')
	if request.method == 'POST':
		form = VideoForm(request.POST)
		if form.is_valid():
			try:
				update_video_info(form, True, request)
			except RuntimeError as e:
				orders = getOrdersForNewVideo(str(request.session['user_id']))
				context = {'form': form, 'error_message': str(e), 'title_text': 'New video', 'role': request.session['user_role']}
				if(len(orders) > 0):
					context['orders'] = orders
				return render(request, 'update_video.html', context)
			
			return redirect('/adv/videos/')
	else:
		form = VideoForm()
		orders = getOrdersForNewVideo(str(request.session['user_id']))
		context = {'form': form, 'title_text': 'New video', 'role': request.session['user_role']}
		if(len(orders) > 0):
			context['orders'] = orders
		return render(request, 'update_video.html', context)

def update_video(request):
	if(not request.session.has_key('user_id')):
		return redirect('/adv/')
	if(request.session['user_role'] != 'Blogger'):
		return redirect('/adv/orders/')
	if request.method == 'POST':
		form = VideoForm(request.POST)
		if form.is_valid():
			try:
				update_video_info(form, False, request)
			except RuntimeError as e:
				context = {'form': form, 'error_message': str(e), 'title_text': 'Update video', 'role': request.session['user_role']}
				return render(request, 'update_video.html', context)
				
			return redirect('/adv/videos')
	else:
		form = VideoForm()
		fill_video_form(form, request.GET.get('video_id', ''))
		request.session['video_id'] = request.GET.get('video_id', '')
		return render(request, 'update_video.html', {'form': form, 'title_text': 'Update video', 'role': request.session['user_role']})
	
def new_order(request):
	if(not request.session.has_key('user_id')):
		return redirect('/adv/')
	if(request.session['user_role'] != 'Advertiser'):
		return redirect('/adv/orders/')
	if request.method == 'POST':
		form = OrderForm(request.POST)
		if form.is_valid():
			try:
				create_order(request.session['user_id'], form)
			except RuntimeError as e:
				context = {'form': form, 'error_message': str(e), 'role': request.session['user_role']}
				return render(request, 'update_order.html', context)
				
			return redirect('/adv/orders/')
	else:
		form = OrderForm()
	return render(request, 'update_order.html', {'form': form})
	
def orders(request):
	if(not request.session.has_key('user_id')):
		return redirect('/adv/')
	orders = get_orders(str(request.session['user_id']), request.session['user_role'])
	context = {'role': request.session['user_role']}
	if(len(orders) == 0):
		context['error_message'] = "You have no orders"
	else:
		context['orders'] = orders
	return render(request, 'orders.html', context)