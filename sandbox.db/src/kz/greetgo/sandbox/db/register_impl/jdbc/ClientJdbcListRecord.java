package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.model.ClientFilter;
import kz.greetgo.sandbox.controller.model.model.ClientRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClientJdbcListRecord implements ConnectionCallback<List<ClientRecord>> {
    public ClientFilter clientFilter;

    List<ClientRecord> clientRecordList = new ArrayList<>();

    public ClientJdbcListRecord(ClientFilter clientFilter) {

        this.clientFilter = clientFilter;

    }


    @Override
    public List<ClientRecord> doInConnection(Connection connection) throws Exception {

        try (PreparedStatement ps = connection.prepareStatement(generateSql().toString())) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    ClientRecord clientRecord = new ClientRecord();
                    clientRecord.id = rs.getInt("id");
                    clientRecord.firstname = rs.getString("firstname");
                    clientRecord.lastname = rs.getString("lastname");
                    clientRecord.patronymic = rs.getString("patronymic");
                    clientRecord.characterName = rs.getString("character");
                    clientRecord.dateOfBirth = rs.getDate("dateOfBirth");
                    clientRecord.maximumBalance = rs.getInt("maximumBalance");
                    clientRecord.minimumBalance = rs.getInt("minimumBalance");
                    clientRecord.totalAccountBalance = rs.getInt("totalAccountBalance");
                    clientRecordList.add(clientRecord);
                }
                rs.close();
                ps.close();
                connection.close();
                return clientRecordList;

            }
        }

    }


    private StringBuilder generateSql() {

        StringBuilder sb = new StringBuilder();
        sb.append("select c.id as id, c.firstname as firstname , c.lastname as lastname, c.patronymic as patronymic, ch.name as character,\n" +
                "       c.birth_date as dateOfBirth, ca.maximumBalance, ca.minimumBalance, ca.totalAccountBalance from client c left join charm ch on c.charm =ch.id\n" +
                "  left join ( select client, avg(money) as totalAccountBalance, max(money) as maximumBalance, min(money) as minimumBalance from client_account ca group by ca.client)\n" +
                "    ca on c.id = ca.client");
        if (!clientFilter.firstname.equals("") || !clientFilter.lastname.equals("") || !clientFilter.patronymic.equals("")) {
            sb.append(" where");
        }
        if (!clientFilter.firstname.equals(""))
            sb.append(" firstname like '" + clientFilter.firstname + "%'");

        if (!clientFilter.lastname.equals("")) {
            if (!clientFilter.firstname.equals(""))
                sb.append(" and");
            sb.append(" lastname like '" + clientFilter.patronymic + "%'");
        }
        if (!clientFilter.patronymic.equals("")) {
            if (!clientFilter.firstname.equals("") || !clientFilter.lastname.equals(""))
                sb.append(" and");
            sb.append(" patronymic like '" + clientFilter.lastname + "%'");
        }
        if (!clientFilter.orderBy.equals("")) {
            sb.append(" order by " + clientFilter.orderBy);
            if (clientFilter.sort)
                sb.append(" asc");

            if (!clientFilter.sort)
                sb.append(" desc");
        }

        sb.append(" limit " + clientFilter.recordSize);
        sb.append(" offset " + (clientFilter.recordSize * clientFilter.page));
        return sb;
    }

}
