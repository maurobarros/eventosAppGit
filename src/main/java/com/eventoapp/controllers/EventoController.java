package com.eventoapp.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventoapp.models.Convidado;
import com.eventoapp.models.Evento;
import com.eventoapp.repository.ConvidadoRepository;
import com.eventoapp.repository.EventoRepository;

@Controller
public class EventoController {

	@Autowired // cria automaticamente uma interface sempre que solicitada (Isto é injecção de dependecias)
	private EventoRepository er;
	
	@Autowired // cria automaticamente uma interface sempre que solicitada
	private ConvidadoRepository cr; 

	// É um get porque vai retornar um formulario
	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.GET)
	public String form() {

		return "formEvento";
	}

	// Para quando clicarmos em "salvar" do nosso formaulario é este metodo que é
	// invocado
	@RequestMapping(value="/cadastrarEvento", method=RequestMethod.POST)
	public String form(@Valid Evento evento, BindingResult result, RedirectAttributes attributes){
		if(result.hasErrors()){
			attributes.addFlashAttribute("mensagem", "Verifique os campos!");
			return "redirect:/cadastrarEvento";
		}
		
		er.save(evento);
		attributes.addFlashAttribute("mensagem", "Evento cadastrado com sucesso!");
		return "redirect:/cadastrarEvento";
	}

	@RequestMapping("/eventos")
	public ModelAndView listaEventos() {
		ModelAndView mv = new ModelAndView("index");//vai rendarizar os dados
		Iterable<Evento> eventos = er.findAll();
		// o "eventos" é da pagina index.html e tem que fazer match com : <div
		// th:each="evento: ${eventos}">
		mv.addObject("eventos", eventos);
		return mv;
	}

	
	
	@RequestMapping(value="/{codigo}", method=RequestMethod.GET)//Aqui vai retornar o codigo de cada evento depois de clicar no nome do cliente
	public ModelAndView detalhesEvento(@PathVariable("codigo") long codigo) {
		Evento evento = er.findByCodigo(codigo);
		ModelAndView mv = new ModelAndView("evento/detalhesEvento");
		// o "evento" catanho em baixo tem que fazer match com   <div th:each="evento :${evento}"> da pagina detalhesEvento
		mv.addObject("evento", evento);
		
		Iterable<Convidado>convidados=cr.findByEvento(evento);
		mv.addObject("convidados",convidados);
		return mv;
	}
	
	
	@RequestMapping("/deletarEvento")
	public String deletarEvento(long codigo) {
		
		Evento evento=er.findByCodigo(codigo);
		er.delete(evento);
		
		return "redirect:/eventos";
	}
	
	
	
	@RequestMapping(value="/{codigo}", method=RequestMethod.POST)
	public String detalhesEventoPost(@PathVariable("codigo") long codigo, @Valid Convidado convidado,BindingResult result, RedirectAttributes attributes){	
		
		if(result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "verifique os campos!!");//envia a mensagem para o utilizador
		return "redirect:/{codigo}";
		}
		
		Evento evento = er.findByCodigo(codigo);
		convidado.setEvento(evento);
		cr.save(convidado);
		attributes.addFlashAttribute("mensagem", "Convidado adicionado com sucesso!!");//envia a mensagem para o utilizador
		return "redirect:/{codigo}";
	}
	
	@RequestMapping("/deletarConvidado")
	public String deletarConvidado(String rg) {
		Convidado convidado=cr.findByRg(rg);
		cr.delete(convidado);
		
		
		Evento evento=convidado.getEvento();
		long codigoLong=evento.getCodigo();
		String codigo=""+codigoLong;
		return "redirect:/"+codigo;
		
	}

}
