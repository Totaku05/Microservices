package microservices.users.service;


import microservices.users.model.ContactInfo;

import java.util.List;

public interface ContactInfoService {
	
	ContactInfo findById(Integer id);

	void saveInfo(ContactInfo info);

	void updateInfo(ContactInfo info);

	void deleteInfoById(Integer id);

	List<ContactInfo> findAllInfos();

	boolean isInfoExist(ContactInfo info);
}