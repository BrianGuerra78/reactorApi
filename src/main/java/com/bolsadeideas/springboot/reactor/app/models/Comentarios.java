package com.bolsadeideas.springboot.reactor.app.models;

import java.util.ArrayList;
import java.util.List;

public class Comentarios {

	private List<String> comantarios;
	
	public Comentarios() {
		this.comantarios = new ArrayList<>();
	}

	public void addComentario(String comantario) {
		this.comantarios.add(comantario);
	}

	@Override
	public String toString() {
		return "Comantarios=" + comantarios;
	}
	
}
