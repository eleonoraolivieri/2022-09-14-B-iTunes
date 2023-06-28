package it.polito.tdp.itunes.model;

public class Adiacenza {
	
	public Adiacenza(Album v1, Album v2) {
		super();
		this.v1 = v1;
		this.v2 = v2;
	}
	private Album v1;
	private Album v2;
	public Album getV1() {
		return v1;
	}
	public void setV1(Album v1) {
		this.v1 = v1;
	}
	public Album getV2() {
		return v2;
	}
	public void setV2(Album v2) {
		this.v2 = v2;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((v1 == null) ? 0 : v1.hashCode());
		result = prime * result + ((v2 == null) ? 0 : v2.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Adiacenza other = (Adiacenza) obj;
		if (v1 == null) {
			if (other.v1 != null)
				return false;
		} else if (!v1.equals(other.v1))
			return false;
		if (v2 == null) {
			if (other.v2 != null)
				return false;
		} else if (!v2.equals(other.v2))
			return false;
		return true;
	}

}
