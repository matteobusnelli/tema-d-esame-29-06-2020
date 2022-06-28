package it.polito.tdp.imdb.model;

public class DirectorAtt {

	private  Director director;
	private Integer attoriCondivisi;
	
	public DirectorAtt(Director director, Integer attoriCondivisi) {
		super();
		this.director = director;
		this.attoriCondivisi = attoriCondivisi;
	}

	public Director getDirector() {
		return director;
	}

	public Integer getAttoriCondivisi() {
		return attoriCondivisi;
	}

	@Override
	public String toString() {
		return director.getFirstName() +" "+director.getLastName()+" ---> "+attoriCondivisi;
	}
	
	
	
}
