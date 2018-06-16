package microservices.users.service;


import microservices.users.model.Advertiser;

import java.util.List;

public interface AdvertiserService {
	
	Advertiser findById(Integer id);

	void saveAdvertiser(Advertiser advertiser);

	void updateAdvertiser(Advertiser advertiser);

	void deleteAdvertiserById(Integer id);

	List<Advertiser> findAllAdvertisers();

	boolean isAdvertiserExist(Advertiser advertiser);
}