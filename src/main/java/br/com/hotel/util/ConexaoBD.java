package br.com.hotel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {

    public static Connection getConexao() throws ClassNotFoundException, SQLException {
        
        // 1. DADOS EXTRAÍDOS DA SUA IMAGEM DO NEON:
        
        // O endereço do servidor (host)
        String host = "ep-muddy-tree-a4v41lwq-pooler.us-east-1.aws.neon.tech";
        
        // O nome do banco de dados
        String database = "hotel_db";
        
        // O usuário (User)
        String user = "neondb_owner";
        
        // A senha (Password) - Copiei exatamente da imagem
        String password = "npg_bNrDs90qCfRa"; 

        // 2. MONTAGEM DA URL JDBC:
        // O Java precisa que a URL comece com "jdbc:postgresql://"
        // E para bancos na nuvem, PRECISA ter "?sslmode=require" no final
        String url = "jdbc:postgresql://" + host + ":5432/" + database + "?sslmode=require";

        // 3. CONEXÃO:
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("ERRO DE CONEXÃO: Verifique se a internet está funcionando.");
            e.printStackTrace();
            throw e;
        }
    }
}