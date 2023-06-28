package it.polito.tdp.itunes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import it.polito.tdp.itunes.model.Adiacenza;
import it.polito.tdp.itunes.model.Album;
import it.polito.tdp.itunes.model.Artist;
import it.polito.tdp.itunes.model.Genre;
import it.polito.tdp.itunes.model.MediaType;
import it.polito.tdp.itunes.model.Playlist;
import it.polito.tdp.itunes.model.Track;

public class ItunesDAO {
	
	public void getAllAlbums(Map<Integer, Album> idMap){
		final String sql = "SELECT * FROM Album";
	
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				if(!idMap.containsKey(res.getInt("AlbumId"))) {
				Album album = new Album(res.getInt("AlbumId"), res.getString("Title"), 0.0);
				idMap.put(res.getInt("AlbumId"), album);
				}
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		
	}
	
	public List<Artist> getAllArtists(){
		final String sql = "SELECT * FROM Artist";
		List<Artist> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Artist(res.getInt("ArtistId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Playlist> getAllPlaylists(){
		final String sql = "SELECT * FROM Playlist";
		List<Playlist> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Playlist(res.getInt("PlaylistId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Track> getAllTracks(){
		final String sql = "SELECT * FROM Track";
		List<Track> result = new ArrayList<Track>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Track(res.getInt("TrackId"), res.getString("Name"), 
						res.getString("Composer"), res.getInt("Milliseconds"), 
						res.getInt("Bytes"),res.getDouble("UnitPrice")));
			
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Genre> getAllGenres(){
		final String sql = "SELECT * FROM Genre";
		List<Genre> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Genre(res.getInt("GenreId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<MediaType> getAllMediaTypes(){
		final String sql = "SELECT * FROM MediaType";
		List<MediaType> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new MediaType(res.getInt("MediaTypeId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Album> getVertici(int durata, Map<Integer,Album> idMap){
		String sql = "SELECT a.AlbumId as id,SUM(t.Milliseconds) as durata "
				+ "FROM Album a, Track t "
				+ "WHERE a.AlbumId = t.AlbumId "
				+ "GROUP BY a.AlbumId "
				+ "HAVING durata > ? * 1000*60 ";
		List<Album> result = new LinkedList<>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, durata);
			
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				result.add(idMap.get(rs.getInt("id")));
			}
			
			rs.close();
			st.close();
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Adiacenza> getAdiacenze(int durata, Map<Integer, Album> idMap) {
		String sql = "With vertici as ( "
				+ "SELECT a.*  "
				+ "FROM Album a, Track t  "
				+ "WHERE a.AlbumId = t.AlbumId "
				+ "GROUP BY a.AlbumId "
				+ "HAVING SUM(t.Milliseconds) > ? * 1000*60) "
				+ "SELECT DISTINCT v1.albumId as id1, v2.albumId as id2 "
				+ "FROM vertici v1, vertici v2, playlistTrack p1, playlistTrack p2, track t1, track t2 "
				+ "WHERE v1.AlbumId < v2.AlbumId "
				+ "AND v1.AlbumId = t1.AlbumId "
				+ "AND v2.AlbumId = t2.AlbumId "
				+ "AND t1.TrackId = p1.TrackId "
				+ "AND t2.TrackId = p2.TrackId "
				+ "AND p1.playlistId = p2.playlistId ";
				
		List<Adiacenza> result = new LinkedList<Adiacenza>();
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, durata);
			
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				Album sorgente = idMap.get(rs.getInt("id1"));
				Album destinazione = idMap.get(rs.getInt("id2"));
				if(sorgente != null && destinazione != null) {
					result.add(new Adiacenza(sorgente, 
							destinazione));
				} else {
					System.out.println("Errore in getAdiacenze");
				}
			}
			rs.close();
			st.close();
			conn.close();
			
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public double getDurata(int id) {
		String sql = "SELECT SUM(t.Milliseconds)/(60*1000) as tot "
				+ "FROM Album a, Track t "
				+ "WHERE a.AlbumId = ? "
				+ "AND a.AlbumId = t.AlbumId ";
				
		double durata = 0.0;
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);
			
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				durata = rs.getInt("tot");
			}
			rs.close();
			st.close();
			conn.close();
			
			return durata;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}
