package br.com.hotel.controller;

import br.com.hotel.dao.HospedeDAO;
import br.com.hotel.model.Hospede;
import br.com.hotel.strategy.IValidador;
import br.com.hotel.strategy.ValidadorCpf;
import br.com.hotel.strategy.ValidadorDadosObrigatorios;
import br.com.hotel.util.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Define a URL que o JavaScript vai chamar
@WebServlet("/api/hospedes")
public class HospedeController extends HttpServlet {

    private HospedeDAO hospedeDAO;
    private Gson gson;
    private List<IValidador> validadores;

    public HospedeController() {
        this.hospedeDAO = new HospedeDAO();
        // Configura o Gson para entender LocalDate
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        
        // Inicializa as estratégias de validação (Strategy Pattern)
        this.validadores = new ArrayList<>();
        this.validadores.add(new ValidadorDadosObrigatorios());
        this.validadores.add(new ValidadorCpf());
    }

    // LISTAR (GET) - RF0104
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            List<Hospede> lista = hospedeDAO.listarTodos();
            String json = gson.toJson(lista);
            out.print(json);
        } catch (Exception e) {
            resp.setStatus(500);
            out.print("{\"erro\": \"Erro ao listar hóspedes: " + e.getMessage() + "\"}");
        }
    }

    // CADASTRAR (POST) - RF0101
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processarRequisicao(req, resp, "CADASTRAR");
    }

    // ATUALIZAR (PUT) - RF0102
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processarRequisicao(req, resp, "ATUALIZAR");
    }

    // INATIVAR (DELETE) - RF0103
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            String idParam = req.getParameter("id");
            if (idParam == null) {
                resp.setStatus(400);
                out.print("{\"erro\": \"ID obrigatório para exclusão.\"}");
                return;
            }
            
            hospedeDAO.inativar(Integer.parseInt(idParam));
            out.print("{\"mensagem\": \"Hóspede inativado com sucesso.\"}");
            
        } catch (Exception e) {
            resp.setStatus(500);
            out.print("{\"erro\": \"Erro ao inativar: " + e.getMessage() + "\"}");
        }
    }

    // Método auxiliar para processar Post e Put (evitar repetição de código)
    private void processarRequisicao(HttpServletRequest req, HttpServletResponse resp, String tipo) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            // 1. Ler o JSON enviado pelo JavaScript
            BufferedReader reader = req.getReader();
            Hospede hospede = gson.fromJson(reader, Hospede.class);

            // 2. Executar validações (Strategy Pattern)
            for (IValidador validador : validadores) {
                String erro = validador.validar(hospede);
                if (erro != null) {
                    resp.setStatus(400); // Bad Request
                    out.print("{\"erro\": \"" + erro + "\"}");
                    return;
                }
            }

            // 3. Executar ação no Banco via DAO
            if ("CADASTRAR".equals(tipo)) {
                hospedeDAO.salvar(hospede);
                resp.setStatus(201); // Created
                out.print("{\"mensagem\": \"Cadastrado com sucesso!\"}");
            } else if ("ATUALIZAR".equals(tipo)) {
                hospedeDAO.atualizar(hospede);
                out.print("{\"mensagem\": \"Atualizado com sucesso!\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            out.print("{\"erro\": \"Erro interno: " + e.getMessage() + "\"}");
        }
    }
}