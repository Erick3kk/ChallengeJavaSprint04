package br.com.fiap.hc.resource;

import br.com.fiap.hc.dao.ConsultaDao;
import br.com.fiap.hc.dto.consulta.AtualizarConsultaDto;
import br.com.fiap.hc.dto.consulta.CadastroConsultaDto;
import br.com.fiap.hc.dto.consulta.DetalhesConsultaDto;
import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Consulta;
import br.com.fiap.hc.model.request.ConsultaRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.modelmapper.ModelMapper;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@Path("/consultas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConsultaResource {

    @Inject
    private ConsultaDao consultaDao;

    @Inject
    private ModelMapper modelMapper;

    @DELETE
    @Path("/deletar/{id}")
    public Response deletar(@PathParam("id") int id) throws EntidadeNaoEncontradaException, SQLException {
        consultaDao.deletar(id);
        return Response.noContent().build(); // 204 No Content
    }

    @PUT
    @Path("/atualizar")
    public Response atualizar(@Valid AtualizarConsultaDto dto)
            throws EntidadeNaoEncontradaException, SQLException {
        Consulta consulta = modelMapper.map(dto, Consulta.class);

        consultaDao.atualizar(consulta);
        return Response.ok().build();
    }

    @GET
    @Path("/buscar/{id}")
    public Response buscar(@PathParam("id") int id) throws SQLException, EntidadeNaoEncontradaException {
        DetalhesConsultaDto dto = modelMapper.map(consultaDao.buscar(id), DetalhesConsultaDto.class);
        return Response.ok(dto).build();
    }


    @GET
    @Path("/listar")
    public List<DetalhesConsultaDto> listar() throws SQLException {
        return consultaDao.listarConsulta().stream()
                .map(c -> modelMapper.map(c, DetalhesConsultaDto.class))
                .toList();
    }

    @GET
    @Path("/consultaPaciente/{idPaciente}")
    public Response consultaPaciente(@PathParam("idPaciente") int idPaciente) throws SQLException, EntidadeNaoEncontradaException {

        List<Consulta> lstConsulta = consultaDao.listarConsultaPaciente(idPaciente);
        return Response.ok(lstConsulta).build();

    }

    @POST
    @Path("/criar")
    public Response create(@Valid CadastroConsultaDto dto,
                           @Context UriInfo uriInfo) throws SQLException {

        ConsultaRequest consulta = modelMapper.map(dto, ConsultaRequest.class);
        consultaDao.cadastrar(consulta);

        return Response.status(201)
                        .entity(consulta)
                        .build();
    }
}