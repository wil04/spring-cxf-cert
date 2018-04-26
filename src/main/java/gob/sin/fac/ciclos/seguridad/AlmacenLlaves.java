package gob.sin.fac.ciclos.seguridad;

import java.io.Serializable;

public final class AlmacenLlaves implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tipo;
	private String archivo;
	private String passAlmacen;
	private String aliasLlavePrivada;
	private String passLlavePrivada;
	
	public AlmacenLlaves() {
	}

	public AlmacenLlaves(String tipo, String archivo, String passAlmacen, String aliasLlavePrivada,
			String passLlavePrivada) {
		super();
		this.tipo = tipo;
		this.archivo = archivo;
		this.passAlmacen = passAlmacen;
		this.aliasLlavePrivada = aliasLlavePrivada;
		this.passLlavePrivada = passLlavePrivada;
	}

	public String getArchivo() {
		return archivo;
	}

	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}

	public String getPassAlmacen() {
		return passAlmacen;
	}

	public void setPassAlmacen(String passAlmacen) {
		this.passAlmacen = passAlmacen;
	}

	public String getAliasLlavePrivada() {
		return aliasLlavePrivada;
	}

	public void setAliasLlavePrivada(String aliasLlavePrivada) {
		this.aliasLlavePrivada = aliasLlavePrivada;
	}

	public String getPassLlavePrivada() {
		return passLlavePrivada;
	}

	public void setPassLlavePrivada(String passLlavePrivada) {
		this.passLlavePrivada = passLlavePrivada;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AlmacenLlaves [tipo=");
		builder.append(tipo);
		builder.append(", archivo=");
		builder.append(archivo);
		builder.append(", passAlmacen=");
		builder.append(passAlmacen);
		builder.append(", aliasLlavePrivada=");
		builder.append(aliasLlavePrivada);
		builder.append(", passLlavePrivada=");
		builder.append(passLlavePrivada);
		builder.append("]");
		return builder.toString();
	}
}
