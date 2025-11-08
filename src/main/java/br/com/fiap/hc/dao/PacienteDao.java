package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Endereco;
import br.com.fiap.hc.model.Paciente;
import br.com.fiap.hc.model.request.PacienteRequest;
import br.com.fiap.hc.model.response.LoginResponse;
import br.com.fiap.hc.resource.PacienteResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static br.com.fiap.hc.utils.Metodos.formatDataHora;

@ApplicationScoped
public class PacienteDao {

    @Inject
    private DataSource dataSource;

    private final EnderecoDao enderecoDao;

    public PacienteDao(DataSource dataSource, EnderecoDao enderecoDao) throws SQLException, ClassNotFoundException {
        this.dataSource = dataSource;
        this.enderecoDao = enderecoDao;
    }

    public void cadastrar(PacienteRequest paciente) throws SQLException {
        String sql = "INSERT INTO T_HC_PACIENTE " +
                "(ID_PACIENTE, NM_NOME, CD_CPF, DT_NASCIMENTO, NR_TELEFONE, DS_EMAIL, ID_ENDERECO) " +
                "VALUES (SQ_HC_PACIENTE.NEXTVAL, ?, ?, TO_DATE( ?, 'YYYY-MM-DD'), ?, ?, ?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"ID_PACIENTE"})) {

            String dataHoraStr = formatDataHora(paciente.getDataNascimento());

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getCpf());
            stmt.setString(3, dataHoraStr);
            stmt.setString(4, paciente.getTelefone());
            stmt.setString(5, paciente.getEmail());
            stmt.setInt(6, paciente.getIdEndereco());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    paciente.setIdPaciente(rs.getInt(1));
                }
            }
        }
    }


    public void atualizar(Paciente paciente) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()) {
            PreparedStatement stmt = conexao.prepareStatement("UPDATE T_HC_PACIENTE SET NM_NOME = ?, NR_TELEFONE = ?, DS_EMAIL = ?, ID_ENDERECO = ? " +
                    " WHERE ID_PACIENTE = ?");

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getTelefone());
            stmt.setString(3, paciente.getEmail());
            stmt.setInt(4, paciente.getEndereco().getIdEndereco());
            stmt.setInt(5, paciente.getIdPaciente());
            stmt.executeUpdate();

            if (stmt.executeUpdate() == 0)
                throw new EntidadeNaoEncontradaException("Não existe Paciente para ser atualizado");
        }
    }


    public void deletar(int idPaciente) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()) {
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM T_HC_PACIENTE WHERE ID_PACIENTE = ?");

            stmt.setInt(1, idPaciente);

            if (stmt.executeUpdate() == 0)
                throw new EntidadeNaoEncontradaException("Não tem Paciente para deletar");
        }
    }

    public Paciente buscar(int idPaciente) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()) {
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_PACIENTE WHERE ID_PACIENTE = ?");
            stmt.setInt(1, idPaciente);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                throw new EntidadeNaoEncontradaException("Paciente não encontrado");
            return parsePaciente(rs);

        }
    }

    public List<Paciente> listar() throws SQLException {
        try (Connection conexao = dataSource.getConnection()) {
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_PACIENTE");
            ResultSet rs = stmt.executeQuery();

            List<Paciente> pacientes = new ArrayList<>();
            while (rs.next()) {
                Paciente paciente = parsePaciente(rs);
                pacientes.add(paciente);
            }
            return pacientes;
        }
    }

    public LoginResponse login(String cpf, String email ) throws SQLException {
        try (Connection conexao = dataSource.getConnection()) {
            PreparedStatement stmt = conexao.prepareStatement(" SELECT * FROM T_HC_PACIENTE " +
                                                                  " WHERE DS_EMAIL = ? " +
                                                                  " AND   CD_CPF   = ? ");
            stmt.setString(1, email);
            stmt.setString(2, cpf);

            ResultSet rs = stmt.executeQuery();

            LoginResponse paciente = new LoginResponse();
            while (rs.next()) {
                paciente = parseLogin(rs);
            }
            return paciente;
        }
    }

    private Paciente parsePaciente(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_PACIENTE");
        String nome = rs.getString("NM_NOME");
        String cpf = rs.getString("CD_CPF");
        Date dataNascimento = rs.getDate("DT_NASCIMENTO");
        String telefone = rs.getString("NR_TELEFONE");
        String email = rs.getString("DS_EMAIL");
        int idEndereco = rs.getInt("ID_ENDERECO");

        Endereco endereco = enderecoDao.buscar(idEndereco);

        return new Paciente(id, nome, cpf, dataNascimento, telefone, email, endereco);
    }
    private LoginResponse parseLogin(ResultSet rs) throws SQLException {
        int id = rs.getInt("ID_PACIENTE");
        String nome = rs.getString("NM_NOME");
        String cpf = rs.getString("CD_CPF");
        String dataNascimento = rs.getString("DT_NASCIMENTO");
        String telefone = rs.getString("NR_TELEFONE");
        String email = rs.getString("DS_EMAIL");
        int idEndereco = rs.getInt("ID_ENDERECO");

        Endereco endereco = enderecoDao.buscar(idEndereco);

        return new LoginResponse(id, nome, cpf, dataNascimento, telefone, email, endereco);
    }

}
