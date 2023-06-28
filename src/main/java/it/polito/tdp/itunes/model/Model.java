package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;


import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	
	private SimpleGraph<Album, DefaultEdge> grafo;
	private ItunesDAO dao;
	private List<Album> album ;
	private Map<Integer,Album> idMap;
	
	public Model() {
		dao = new ItunesDAO();
		idMap = new HashMap<Integer,Album>();
		dao.getAllAlbums(idMap);
	}
	
	public void creaGrafo(int durata) {
		
		this.grafo = new SimpleGraph<>(DefaultEdge.class);
		
		this.album = dao.getVertici(durata,idMap);
		Graphs.addAllVertices(grafo, album);
		
		for(Adiacenza a : dao.getAdiacenze(durata,idMap)) {
			if(this.grafo.containsVertex(a.getV1()) && 
					this.grafo.containsVertex(a.getV2())) {
				DefaultEdge e = this.grafo.getEdge(a.getV1(), a.getV2());
				if(e == null) 
					Graphs.addEdgeWithVertices(grafo, a.getV1(), a.getV2());
			}
		}
		
		System.out.println("VERTICI: " + this.grafo.vertexSet().size());
		System.out.println("ARCHI: " + this.grafo.edgeSet().size());
	}
	
	public Set<Album> calcolaComponentiConnesse(Album a){
		ConnectivityInspector<Album, DefaultEdge> inspect = new ConnectivityInspector<Album, DefaultEdge>(this.grafo);
		System.out.println("La dimensione della componente è: " + inspect.connectedSetOf(a).size());
		return inspect.connectedSetOf(a);
	}
	
	public double calcolaDurataTot(Album a){
		ConnectivityInspector<Album, DefaultEdge> inspect = new ConnectivityInspector<Album, DefaultEdge>(this.grafo);
		
		Set<Album> selezionati =  inspect.connectedSetOf(a);
		double durata = 0;
		
		
		for(Album s: selezionati) {
			int id = s.getAlbumId();
			durata = durata +dao.getDurata(id) ;
		}
		
		return durata;
	}
	
	public List<Album> getVertici(){
		return new ArrayList<Album>(this.grafo.vertexSet());
	}
	
	public int getNVertici(){
		return this.grafo.vertexSet().size();
	}
	
	
	/**
	 * Metodo che restituisce il numero di archi del grafo
	 * @return
	 */
	public int getNArchi(){
		return this.grafo.edgeSet().size();
	}
	
	
	
	
	
}
