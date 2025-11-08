package br.com.fiap.hc.resource;

import br.com.fiap.hc.dao.PacienteDao;
import br.com.fiap.hc.dto.paciente.AtualizarPacienteDto;
import br.com.fiap.hc.dto.paciente.CadastroPacienteDto;
import br.com.fiap.hc.dto.paciente.DetalhesPacienteDto;
import br.com.fiap.hc.dto.paciente.LoginPacienteDto;
import br.com.fiap.hc.exception.EntidadeNaoEncontradaException;
import br.com.fiap.hc.model.Paciente;
import br.com.fiap.hc.model.request.PacienteRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.modelmapper.ModelMapper;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@Path("/pacientes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PacienteResource {

    @Inject
    private PacienteDao pacienteDao;

    @Inject
    private ModelMapper modelMapper;

    @DELETE
    @Path("/deletar/{id}")
    public Response deletar(@PathParam("id") int id) throws EntidadeNaoEncontradaException, SQLException {
        pacienteDao.deletar(id);
        return Response.noContent().build(); // 204 No Content
    }

    @PUT
    @Path("/atualizar/{id}")
    public Response atualizar(@PathParam("id") int id, @Valid AtualizarPacienteDto dto)
            throws EntidadeNaoEncontradaException, SQLException {
        Paciente paciente = modelMapper.map(dto, Paciente.class);
        paciente.setIdPaciente(id);
        pacienteDao.atualizar(paciente);
        return Response.ok().build();
    }

    @GET
    @Path("/buscar/{id}")
    public Response buscar(@PathParam("id") int id) throws SQLException, EntidadeNaoEncontradaException {
        DetalhesPacienteDto dto = modelMapper.map(pacienteDao.buscar(id), DetalhesPacienteDto.class);
        return Response.ok(dto).build();
    }

    @GET
    @Path("/listar")
    public List<DetalhesPacienteDto> listar() throws SQLException {
        return pacienteDao.listar().stream()
                .map(p -> modelMapper.map(p, DetalhesPacienteDto.class))
                .toList();
    }

    @POST
    @Path("/criar")
    public Response create(@Valid CadastroPacienteDto dto,
                           @Context UriInfo uriInfo) throws SQLException {

        PacienteRequest paciente = modelMapper.map(dto, PacienteRequest.class);
        pacienteDao.cadastrar(paciente);

        // Constrói a URL para o recurso criado
        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(paciente.getIdPaciente()))
                .build();

        return Response.created(uri)
                .entity(modelMapper.map(paciente, DetalhesPacienteDto.class))
                .build(); // 201 Created
    }
}