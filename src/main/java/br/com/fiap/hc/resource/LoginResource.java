package br.com.fiap.hc.resource;

import br.com.fiap.hc.dao.PacienteDao;
import br.com.fiap.hc.dto.paciente.DetalhesPacienteDto;
import br.com.fiap.hc.dto.paciente.LoginPacienteDto;
import br.com.fiap.hc.model.Paciente;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.modelmapper.ModelMapper;

import java.net.URI;
import java.sql.SQLException;

@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    private PacienteDao pacienteDao;

    @Inject
    private ModelMapper modelMapper;

    @POST
    public Response login(@Valid LoginPacienteDto dto,
                          @Context UriInfo uriInfo) throws SQLException {

        Paciente paciente = modelMapper.map(dto, Paciente.class);
        paciente = pacienteDao.login(paciente.getCpf(), paciente.getEmail());

        // Constrói a URL para o recurso criado
        return Response.ok(paciente).build();
    }
}
