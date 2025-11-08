package br.com.fiap.hc.resource;

import br.com.fiap.hc.dao.ReceitaDao;
import br.com.fiap.hc.dto.receita.AtualizarReceitaDto;
import br.com.fiap.hc.dto.receita.CadastroReceitaDto;
import br.com.fiap.hc.dto.receita.DetalhesReceitaDto;
import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Consulta;
import br.com.fiap.hc.model.Receita;
import br.com.fiap.hc.model.request.ReceitaRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.modelmapper.ModelMapper;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@Path("/receitas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReceitaResource {

    @Inject
    private ReceitaDao receitaDao;

    @Inject
    private ModelMapper modelMapper;

    @DELETE
    @Path("/deletar/{id}")
    public Response deletar(@PathParam("id") int id) throws EntidadeNaoEncontradaException, SQLException {
        receitaDao.deletar(id);
        return Response.noContent().build(); // 204 No Content
    }

    @PUT
    @Path("/atualizar/{id}")
    public Response atualizar(@PathParam("id") int id, @Valid AtualizarReceitaDto dto)
            throws EntidadeNaoEncontradaException, SQLException {
        Receita receita = modelMapper.map(dto, Receita.class);
        receita.setIdReceita(id);
        receitaDao.atualizar(receita);
        return Response.ok().build();
    }

    @GET
    @Path("/buscar/{id}")
    public Response buscar(@PathParam("id") int id) throws SQLException, EntidadeNaoEncontradaException {
        DetalhesReceitaDto dto = modelMapper.map(receitaDao.buscar(id), DetalhesReceitaDto.class);
        return Response.ok(dto).build();
    }


    @GET
    @Path("/listar")
    public List<DetalhesReceitaDto> listar() throws SQLException {
        return receitaDao.listar().stream()
                .map(r -> modelMapper.map(r, DetalhesReceitaDto.class))
                .toList();
    }

    @GET
    @Path("/receitaConsulta/{idConsulta}")
    public Response receitaConsulta (@PathParam("idConsulta") int idConsulta) throws SQLException, EntidadeNaoEncontradaException {

        List<Receita> lstReceitas = receitaDao.listarReceitaConsulta(idConsulta);
        return Response.ok(lstReceitas).build();

    }

    @POST
    @Path("/criar")
    public Response create(@Valid CadastroReceitaDto dto,
                           @Context UriInfo uriInfo) throws SQLException {

        ReceitaRequest receitaRequest = modelMapper.map(dto, ReceitaRequest.class);
        Receita receita = receitaDao.cadastrar(receitaRequest);

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(receita.getIdReceita()))
                .build();

        return Response.created(uri)
                .entity(receita)
                .build();
    }
}