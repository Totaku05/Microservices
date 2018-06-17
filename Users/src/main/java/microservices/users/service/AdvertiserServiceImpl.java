package microservices.users.service;

import microservices.users.model.Advertiser;
import microservices.users.repositories.AdvertiserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("advertiserService")
@Transactional
public class AdvertiserServiceImpl implements AdvertiserService {

	@Autowired
	private AdvertiserRepository advertiserRepository;

	public Advertiser findById(Integer id)
	{
		return advertiserRepository.findOne(id);
	}

	public void saveAdvertiser(Advertiser advertiser) {
		advertiserRepository.save(advertiser);
	}

	public void updateAdvertiser(Advertiser advertiser){
		saveAdvertiser(advertiser);
	}

	public void deleteAdvertiserById(Integer id){
		advertiserRepository.delete(id);
	}

	public List<Advertiser> findAllAdvertisers(){
		return advertiserRepository.findAll();
	}

	public boolean isAdvertiserExist(Advertiser advertiser) {
		return findById(advertiser.getId()) != null;
	}

	public void newAdvertiser(Integer id, String login, Integer card_number)
    {
        Advertiser advertiser = new Advertiser();
        advertiser.setId(id);
        advertiser.setAccount(0.0);
        advertiser.setLogin(login);
        advertiser.setCard_number(card_number);
        saveAdvertiser(advertiser);
    }

    public Integer updateAccount(Integer id, Double sum)
    {
        Advertiser advertiser = findById(id);

        if(advertiser.getAccount() + sum < 0)
            return -1;

        advertiser.setAccount(advertiser.getAccount() + sum);
        updateAdvertiser(advertiser);

        return advertiser.getCard_number();
    }

}
