package microservices.users.service;

import microservices.users.model.ContactInfo;
import microservices.users.repositories.ContactInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("contactInfoService")
@Transactional
public class ContactInfoServiceImpl implements ContactInfoService{

	@Autowired
	private ContactInfoRepository contactInfoRepository;

	public ContactInfo findById(Integer id)
	{
		return contactInfoRepository.findOne(id);
	}

	public void saveInfo(ContactInfo info) {
		contactInfoRepository.save(info);
	}

	public void updateInfo(ContactInfo info){
		saveInfo(info);
	}

	public void deleteInfoById(Integer id){
		contactInfoRepository.delete(id);
	}

	public List<ContactInfo> findAllInfos(){
		return contactInfoRepository.findAll();
	}

	public boolean isInfoExist(ContactInfo info) {
		return findById(info.getId()) != null;
	}

}
