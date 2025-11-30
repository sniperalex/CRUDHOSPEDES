package br.com.hotel.dao;

import br.com.hotel.model.Hospede;
import br.com.hotel.util.ConexaoBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HospedeDAO {

    public void salvar(Hospede h) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO hospedes (nome, cpf, data_nascimento, telefone, email, logradouro, numero, cep, bairro, complemento, cidade, estado, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, h.getNome());
            stmt.setString(2, h.getCpf());
            stmt.setDate(3, Date.valueOf(h.getDataNascimento()));
            stmt.setString(4, h.getTelefone());
            stmt.setString(5, h.getEmail());
            stmt.setString(6, h.getLogradouro());
            stmt.setString(7, h.getNumero());
            stmt.setString(8, h.getCep());
            stmt.setString(9, h.getBairro());
            stmt.setString(10, h.getComplemento());
            stmt.setString(11, h.getCidade());
            stmt.setString(12, h.getEstado());
            stmt.setBoolean(13, h.getAtivo());

            stmt.execute();
        }
    }

    public List<Hospede> listarTodos() throws SQLException, ClassNotFoundException {
        List<Hospede> lista = new ArrayList<>();
        String sql = "SELECT * FROM hospedes WHERE ativo = true ORDER BY nome";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Hospede h = new Hospede();
                h.setId(rs.getInt("id"));
                h.setNome(rs.getString("nome"));
                h.setCpf(rs.getString("cpf"));
                h.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                h.setTelefone(rs.getString("telefone"));
                h.setEmail(rs.getString("email"));
                h.setLogradouro(rs.getString("logradouro"));
                h.setNumero(rs.getString("numero"));
                h.setCep(rs.getString("cep"));
                h.setBairro(rs.getString("bairro"));
                h.setComplemento(rs.getString("complemento"));
                h.setCidade(rs.getString("cidade"));
                h.setEstado(rs.getString("estado"));
                h.setAtivo(rs.getBoolean("ativo"));
                lista.add(h);
            }
        }
        return lista;
    }

    public void atualizar(Hospede h) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE hospedes SET nome=?, data_nascimento=?, telefone=?, email=?, logradouro=?, numero=?, cep=?, bairro=?, complemento=?, cidade=?, estado=? WHERE id=?";

        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, h.getNome());
            stmt.setDate(2, Date.valueOf(h.getDataNascimento()));
            stmt.setString(3, h.getTelefone());
            stmt.setString(4, h.getEmail());
            stmt.setString(5, h.getLogradouro());
            stmt.setString(6, h.getNumero());
            stmt.setString(7, h.getCep());
            stmt.setString(8, h.getBairro());
            stmt.setString(9, h.getComplemento());
            stmt.setString(10, h.getCidade());
            stmt.setString(11, h.getEstado());
            stmt.setInt(12, h.getId());

            stmt.execute();
        }
    }

    public void inativar(Integer id) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE hospedes SET ativo = false WHERE id = ?";
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
        }
    }
}