package br.com.fiap.hc.dao;

import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Consulta;
import br.com.fiap.hc.model.Medico;
import br.com.fiap.hc.model.Paciente;
import br.com.fiap.hc.model.Receita;
import br.com.fiap.hc.model.request.ReceitaRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

import static br.com.fiap.hc.utils.Metodos.formatDataHora;

@ApplicationScoped
public class ReceitaDao {

    @Inject
    private DataSource dataSource;

    private final PacienteDao pacienteDao;
    private final MedicoDao medicoDao;
    private final ConsultaDao consultaDao;


    public ReceitaDao(DataSource dataSource, PacienteDao pacienteDao, MedicoDao medicoDao, ConsultaDao consultaDao, EnderecoDao enderecoDao) throws SQLException, ClassNotFoundException {
        this.dataSource = dataSource;
        this.consultaDao = new ConsultaDao(this.dataSource, enderecoDao);
        this.pacienteDao = new PacienteDao(this.dataSource, enderecoDao);
        this.medicoDao = new MedicoDao(this.dataSource);
    }

    public Receita cadastrar(ReceitaRequest receita) throws SQLException {
        try (Connection conexao = dataSource.getConnection()) {

            PreparedStatement stmt = conexao.prepareStatement("INSERT INTO T_HC_RECEITA (ID_RECEITA, DS_MEDICAMENTO, DS_DOSAGEM, DT_EMISSAO, ID_CONSULTA)" +
                    " VALUES (SQ_HC_RECEITA.nextval, ?, ?, sysdate, ?)", new String[]{"ID_RECEITA"});

            stmt.setString(1, receita.getMedicamento());
            stmt.setString(2, receita.getDosagem());
            stmt.setInt(3, receita.getIdConsulta());
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();

            int idReceita = 0;
            if (resultSet.next()) {
                idReceita = resultSet.getInt(1);
            }

            return buscar(idReceita);
        }
    }

    public void atualizar(Receita receita) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()) {
            PreparedStatement stmt = conexao.prepareStatement("UPDATE T_HC_RECEITA SET DS_MEDICAMENTO = ?, DS_DOSAGEM = ?, DT_EMISSAO = ?, ID_CONSULTA = ? \" +\n" +
                    "                        \"WHERE ID_RECEITA = ?");

            stmt.setString(1, receita.getMedicamento());
            stmt.setString(2, receita.getDosagem());
            stmt.setDate(3, new java.sql.Date(receita.getDataEmissao().getTime()));
            stmt.setInt(4, receita.getConsulta().getIdConsulta());
            stmt.setInt(5, receita.getIdReceita());
            stmt.executeUpdate();

            if (stmt.executeUpdate() == 0)
                throw new EntidadeNaoEncontradaException("Não existe Receita para ser atualizado");
        }
    }

    public void deletar(int idReceita) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()) {
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM T_HC_RECEITA WHERE ID_RECEITA = ?");
            stmt.setInt(1, idReceita);

            if (stmt.executeUpdate() == 0)
                throw new EntidadeNaoEncontradaException("Não tem Receita para deletar");
        }
    }

    public Receita buscar(int idReceita) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = dataSource.getConnection()) {
            PreparedStatement stmt = conexao.prepareStatement("SELECT ID_RECEITA, DS_MEDICAMENTO, DS_DOSAGEM, DT_EMISSAO, R.ID_CONSULTA, C.ID_MEDICO, C.ID_PACIENTE" +
                    " FROM T_HC_CONSULTA C INNER JOIN T_HC_RECEITA R ON C.ID_CONSULTA = R.ID_CONSULTA WHERE R.ID_RECEITA = ?");

            stmt.setInt(1, idReceita);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next())
                throw new EntidadeNaoEncontradaException("Receita não encontrado");
            return parseReceita(rs);
        }
    }


    public List<Receita> listar() throws SQLException {
        try (Connection conexao = dataSource.getConnection()) {
            PreparedStatement stmt = conexao.prepareStatement("SELECT ID_RECEITA, DS_MEDICAMENTO, DS_DOSAGEM, DT_EMISSAO, R.ID_CONSULTA, C.ID_MEDICO, C.ID_PACIENTE" +
                    " FROM T_HC_CONSULTA C INNER JOIN T_HC_RECEITA R ON C.ID_CONSULTA = R.ID_CONSULTA");
            ResultSet rs = stmt.executeQuery();
            List<Receita> lista = new ArrayList<>();

            while (rs.next()) {
                Receita receitas = parseReceita(rs);
                lista.add(receitas);
            }
            return lista;
        }
    }

    public List<Receita> listarReceitaConsulta(int idConsulta) throws SQLException {
        try (Connection conexao = dataSource.getConnection()) {
            PreparedStatement stmt = conexao.prepareStatement("SELECT * FROM T_HC_RECEITA WHERE ID_CONSULTA = ? ");
            stmt.setInt(1, idConsulta);
            ResultSet rs = stmt.executeQuery();
            List<Receita> receitas = new ArrayList<>();
            while (rs.next()) {
                Receita receita = parseReceitaConsulta(rs);
                receitas.add(receita);
            }
            return receitas;
        }
    }

    private Receita parseReceita(ResultSet rs) throws SQLException {
        int idPaciente = rs.getInt("ID_PACIENTE");
        int idMedico = rs.getInt("ID_MEDICO");
        int idConsulta = rs.getInt("ID_CONSULTA");

        Paciente paciente = pacienteDao.buscar(idPaciente);
        Medico medico = medicoDao.buscar(idMedico);
        Consulta consulta = consultaDao.buscar(idConsulta);

        consulta.setPaciente(paciente);
        consulta.setMedico(medico);

        Receita receita = new Receita();
        receita.setIdReceita(rs.getInt("ID_RECEITA"));
        receita.setMedicamento(rs.getString("DS_MEDICAMENTO"));
        receita.setDosagem(rs.getString("DS_DOSAGEM"));
        receita.setDataEmissao(rs.getDate("DT_EMISSAO"));
        receita.setConsulta(consulta);

        return receita;
    }

    private Receita parseReceitaConsulta(ResultSet rs) throws SQLException {
        int idConsulta = rs.getInt("ID_CONSULTA");

        Consulta consulta = consultaDao.buscar(idConsulta);

        Receita receita = new Receita();
        receita.setIdReceita(rs.getInt("ID_RECEITA"));
        receita.setMedicamento(rs.getString("DS_MEDICAMENTO"));
        receita.setDosagem(rs.getString("DS_DOSAGEM"));
        receita.setDataEmissao(rs.getDate("DT_EMISSAO"));
        receita.setConsulta(consulta);

        return receita;
    }
}
