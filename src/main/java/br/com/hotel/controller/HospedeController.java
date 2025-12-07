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

@WebServlet("/api/hospedes")
public class HospedeController extends HttpServlet {

    private HospedeDAO hospedeDAO;
    private Gson gson;
    private List<IValidador> validadores;

    public HospedeController() {
        this.hospedeDAO = new HospedeDAO();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        
        this.validadores = new ArrayList<>();
        this.validadores.add(new ValidadorDadosObrigatorios());
        this.validadores.add(new ValidadorCpf());
    }

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
            out.print("{\"erro\": \"Erro ao listar: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processarRequisicao(req, resp, "CADASTRAR");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processarRequisicao(req, resp, "ATUALIZAR");
    }

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

    private void processarRequisicao(HttpServletRequest req, HttpServletResponse resp, String tipo) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            BufferedReader reader = req.getReader();
            Hospede hospede = gson.fromJson(reader, Hospede.class);

            // Validações (Strategy)
            for (IValidador validador : validadores) {
                String erro = validador.validar(hospede);
                if (erro != null) {
                    resp.setStatus(400); 
                    out.print("{\"erro\": \"" + erro + "\"}");
                    return;
                }
            }

            if ("CADASTRAR".equals(tipo)) {
                hospedeDAO.salvar(hospede);
                resp.setStatus(201);
                out.print("{\"mensagem\": \"Cadastrado com sucesso!\"}");
            } else if ("ATUALIZAR".equals(tipo)) {
                hospedeDAO.atualizar(hospede);
                out.print("{\"mensagem\": \"Atualizado com sucesso!\"}");
            }

        } catch (Exception e) {
            // --- AQUI ESTÁ A CORREÇÃO ---
            String erroMsg = e.getMessage();
            
            // Verifica se o erro do banco diz "duplicate key" (padrão do PostgreSQL)
            if (erroMsg != null && erroMsg.contains("duplicate key")) {
                resp.setStatus(409); // Código HTTP 409 = Conflito
                out.print("{\"erro\": \"Este CPF já está cadastrado no sistema.\"}");
            } else {
                e.printStackTrace(); // Mostra erro no terminal
                resp.setStatus(500);
                // Escapamos as aspas para não quebrar o JSON
                String msgLimpa = erroMsg != null ? erroMsg.replace("\"", "'") : "Erro desconhecido";
                out.print("{\"erro\": \"Erro interno: " + msgLimpa + "\"}");
            }
        }
    }
}