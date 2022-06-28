package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private ImdbDAO dao;
	private Graph<Director, DefaultWeightedEdge> grafo;
	private Map<Integer, Director> idMap;
	
	public Model() {
		this.dao = new ImdbDAO();
		this.idMap = new HashMap<Integer, Director>();
		
	}
	
	public String creaGrafo(Integer anno) {
		
		this.dao.listAllDirectors(idMap);
		this.grafo = new SimpleWeightedGraph<Director, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//vertici
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(idMap, anno));
		
		//archi
		for(Adiacenza a : this.dao.getArchi(idMap, anno)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getD1(), a.getD2(), a.getPeso());
		}
		
		
		System.out.println("Grafo creato\n#vertici "+this.grafo.vertexSet().size()+"\n#archi "+this.grafo.edgeSet().size()+"\n");
		return "Grafo creato\n#vertici "+this.grafo.vertexSet().size()+"\n#archi "+this.grafo.edgeSet().size()+"\n";
	}
	
	
	public List<DirectorAtt> getDirectorAtt(Director scelto){
		List<DirectorAtt> result = new ArrayList<>();
		List<Director> vicini = Graphs.neighborListOf(this.grafo, scelto); //solo perche il grafo non è orientato, senno userei successorList o predecessorList
		for(Director d : vicini) {
			result.add(new DirectorAtt(d, (int) this.grafo.getEdgeWeight(this.grafo.getEdge(scelto, d))));
		}
		
		Collections.sort(result, new Comparator<DirectorAtt>() {

			@Override
			public int compare(DirectorAtt o1, DirectorAtt o2) {
				return - o1.getAttoriCondivisi().compareTo(o2.getAttoriCondivisi());
			}
		});
		
		return result;
	}
	
	
	
	public Set<Director> getDirector(){
		return this.grafo.vertexSet();
	}
	
}
