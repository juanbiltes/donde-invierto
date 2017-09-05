package edu.utn.frba.dds.grupo5.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.utn.frba.dds.grupo5.entidades.Cuenta;
import edu.utn.frba.dds.grupo5.entidades.Empresa;
import edu.utn.frba.dds.grupo5.entidades.Indicador;
import edu.utn.frba.dds.grupo5.indicadores.IndicadorException;
import edu.utn.frba.dds.grupo5.service.ServiceManager;

public class TestPersistencia {
	//	TODO
	
	private static List<Empresa> empresas;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() throws IndicadorException, Exception {
		String empresasString = IOUtils.toString(TestIndicadores.class.getClassLoader().getResource("empresas.json"));
		Type listType = new TypeToken<ArrayList<Empresa>>(){}.getType();
		empresas = ((List<Empresa>)new Gson().fromJson(empresasString, listType));
		
		String cuentasString = IOUtils.toString(TestIndicadores.class.getClassLoader().getResource("cuentas.json"));
		listType = new TypeToken<ArrayList<Cuenta>>(){}.getType();
		List<Cuenta> cuentas = ((List<Cuenta>)new Gson().fromJson(cuentasString, listType));
		
		ServiceManager.getInstance().guardarCuentas(cuentas);
		
		String indicadoresString = IOUtils.toString(TestIndicadores.class.getClassLoader().getResource("indicadores-predefinidos.json"));
		listType = new TypeToken<ArrayList<Indicador>>(){}.getType();
		List<Indicador> indicadores = ((List<Indicador>)new Gson().fromJson(indicadoresString, listType));
		
		ServiceManager.getInstance().guardarIndicadores(indicadores);
	}
	
	@Test
	public void testEmpresa() throws Exception{
		for(Empresa emp: empresas){
			ServiceManager.getInstance().guardarEmpresa(emp);
		}
		
		Empresa snapchat = ServiceManager.getInstance().buscarEmpresa("Snapchat");
		
		assertEquals("Snapchat",snapchat.getNombre());
		assertEquals(Integer.valueOf(2011),snapchat.getAnioFundacion());
		assertEquals(2,snapchat.getPeriodos().size());
	} 

	
	@Test
	public void testPeriodos() throws Exception{
		Empresa apple = ServiceManager.getInstance().buscarEmpresa("Apple");
		assertEquals("Apple",apple.getNombre());
		assertEquals(11,apple.getPeriodos().size());
		assertEquals("Anual",apple.getPeriodos().get(0).getNombre());
		assertEquals("Anual",apple.getPeriodos().get(6).getNombre());
		assertEquals("2011",apple.getPeriodos().get(6).getAnio());
		assertEquals(0,apple.getPeriodos().get(3).getStartRange());
		assertEquals(12,apple.getPeriodos().get(8).getEndRange());
		assertEquals(5,apple.getPeriodos().get(9).getCuentas().size());
	}
	
	@Test
	public void testMetodologias() throws Exception, IndicadorException{

		empresas = ServiceManager.getInstance().evaluateMetodologia("Buffet", empresas);
		
		 assertTrue(empresas.size()==3);
		 assertEquals("Pepsico",empresas.get(0).getNombre());
		 assertEquals("Apple",empresas.get(1).getNombre());
		 assertEquals("Google",empresas.get(2).getNombre());
		
	}
	
@AfterClass
	public static void clearDatabase() throws Exception{
		ServiceManager.getInstance().clearRepo();
	}	
	
}