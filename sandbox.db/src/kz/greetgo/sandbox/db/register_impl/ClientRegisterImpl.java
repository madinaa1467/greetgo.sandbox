package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.db.Jdbc;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.register_impl.jdbc.ClientJdbc;
import kz.greetgo.sandbox.db.register_impl.jdbc.ClientJdbcListRecord;

import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

	public BeanGetter<ClientDao> clientDao;

	public BeanGetter<Jdbc> jdbc;

	@Override
	public List<Charm> getCharm() {

		return clientDao.get().listCharm();
	}

	@Override
	public void deleteClient(Integer clientMarkId) {

		clientDao.get().deleteClientById(clientMarkId);
	}

	@Override
	public Charm getCharmById(Integer charmId) {

		if (charmId == null) {
			return null;
		}
		Charm charm = new Charm();
		Integer id = clientDao.get().idCharmById(charmId);
		if (id != null) {
			return clientDao.get().selectCharmById(charmId);
		}
		if (id == null) {
			charm.id = charmId;
			charm.name = String.valueOf(charmId);
			charm.description = String.valueOf(charmId);
			charm.energy = charmId;
			charm.actually = false;
		}

		return charm;
	}

	@Override
	public ClientDetails getClientDetails(Integer clientMarkId) {

		ClientDetails clientDetails = new ClientDetails();
		Client client = clientDao.get().selectClientById(clientMarkId);
		clientDetails.addressOfResidence = clientDao.get().selectClientAddrById(clientMarkId, AddrType.FACT);
		clientDetails.addressOfRegistration = clientDao.get().selectClientAddrById(clientMarkId, AddrType.REG);
		for (ClientPhone clientPhone : clientDao.get().selectClientPhoneById(clientMarkId)) {
			clientDetails.phone.add(clientPhone);
		}
		clientDetails.id = client.id;
		clientDetails.firstname = client.firstname;
		clientDetails.lastname = client.lastname;
		clientDetails.patronymic = client.patronymic;
		clientDetails.dateOfBirth = client.birthDate;
		clientDetails.gender = client.gender;
		clientDetails.characterId = client.charm;

		return clientDetails;
	}

	@Override
	public ClientRecord saveClient(ClientToSave clientToSave) {

		Integer id = clientDao.get().getClientId(clientToSave.id);
		ClientAddr clientAddr;
		ClientRecord clientRecord = new ClientRecord();
		Client client = new Client();
		if (id == null) {
			clientDao.get().insertClient(clientToSave);
			clientDao.get().insertClientAddr(clientToSave.addressOfResidence, clientToSave.id);
			clientDao.get().insertClientAddr(clientToSave.addressOfRegistration, clientToSave.id);
			for (ClientPhone clientPhone : clientToSave.phone) {
				clientDao.get().insertClientPhone(clientPhone, clientToSave.id);
			}

		}

		if (id != null) {
			clientDao.get().updateClient(clientToSave);
			for (ClientPhone clientPhone : clientToSave.phone) {
				clientDao.get().updateClientPhone(clientPhone, clientToSave.id);
			}
			clientAddr = clientToSave.addressOfResidence;
			clientDao.get().updateClientAddr(clientToSave.id, clientAddr);
			clientAddr = clientToSave.addressOfRegistration;
			clientDao.get().updateClientAddr(clientToSave.id, clientAddr);
		}
		client = clientDao.get().selectClientById(clientToSave.id);
		clientRecord.id = client.id;
		clientRecord.firstname = client.firstname;
		clientRecord.lastname = client.lastname;
		clientRecord.patronymic = client.patronymic;
		clientRecord.dateOfBirth = client.birthDate;
		clientRecord.characterName = clientDao.get().nameCharmById(clientToSave.characterId);
		clientRecord.totalAccountBalance = clientDao.get().selectTotalAccountBalance(clientToSave.id);
		clientRecord.maximumBalance = clientDao.get().selectMaximumBalance(clientToSave.id);
		clientRecord.minimumBalance = clientDao.get().selectMinimumBalance(clientToSave.id);

		System.err.println("ClientRecordsss:" + clientRecord);
		return clientRecord;
	}

	@Override
	public List<ClientRecord> getClientList(ClientFilter clientFilter) {


		List<ClientRecord> clientRecords = new ArrayList<>();
		clientRecords = jdbc.get().execute(new ClientJdbcListRecord(clientFilter));
		return clientRecords;
	}

	@Override
	public Integer getClientTotalRecord(ClientFilter clientFilter) {

		clientFilter.recordTotal = jdbc.get().execute(new ClientJdbc(clientFilter));
		return clientFilter.recordTotal;

	}


}
