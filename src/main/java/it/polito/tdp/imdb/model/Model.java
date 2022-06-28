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
	private List<Director> best;
	private int totAttoriCondivisi;
	
	public Model() {
		this.dao = new ImdbDAO();
		this.idMap = new HashMap<Integer, Director>();
		this.totAttoriCondivisi = 0;
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
	
	
	
	public List<Director> calcolaPercorsoMax(Director partenza, int c){
		this.best = new ArrayList<>();
		List<Director> parziale = new ArrayList<>();
		
		
		parziale.add(partenza);
		
		cerca(parziale, c);
		return best;
	}
	
	
	private void cerca(List<Director> parziale, int c) {

		//condizione di terminazione
		if(parziale.size()>best.size()) {
				best = new ArrayList<Director>(parziale);
				this.totAttoriCondivisi = sommaPesi(this.best);
			}
			
		List<Director> vicini = Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1)) ;

		
		for(Director d : vicini) {
			if(!parziale.contains(d) && (sommaPesi(parziale)+this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(parziale.size()-1),d)))<=c) {
				parziale.add(d) ;
				cerca(parziale, c) ;
				parziale.remove(parziale.size()-1) ;
			}
			}
		}
		

	private int sommaPesi(List<Director> parziale) {
		int peso = 0;
		for(int i=1; i<parziale.size(); i++) {
			double p = this.grafo.getEdgeWeight(this.grafo.getEdge(parziale.get(i-1), parziale.get(i))) ;
			peso += p ;
		}
		return peso ;
	}

	public Set<Director> getDirector(){
		return this.grafo.vertexSet();
	}

	public int getTotAttoriCondivisi() {
		return totAttoriCondivisi;
	}
	
}
