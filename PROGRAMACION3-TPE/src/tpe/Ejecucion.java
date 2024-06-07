package tpe;

import tpe.utils.Tarea;

import tpe.utils.Procesador;
import tpe.utils.Solucion;

import java.util.ArrayList;
import java.util.List;

public class Ejecucion {
    private ArrayList<Procesador> procesadores;
    private ArrayList<Tarea> tareas;
    private int estadosGenerados;
    private Solucion mejorSolucion;
    private int candidatosConsiderados;
    private Servicios servicio;
    public Ejecucion(List<Procesador> procesadores, List<Tarea> tareas){
        procesadores = new ArrayList<>(procesadores);
        tareas = new ArrayList<>(tareas);
        this.estadosGenerados = 0;
        this.mejorSolucion = null;
        this.candidatosConsiderados = 0;
        this.servicio = new Servicios("./src/tpe/datasets/Procesadores.csv", "./src/tpe/datasets/Tareas.csv");
    }

    public void backtracking(int tiempoMax){
        String respuesta = "";
        if(tareas.isEmpty()){
            System.out.println("Proceso terminado");

        }
        else{
            boolean [] fueCritica = new boolean[procesadores.size()];
            boolean backtrack = backtrack(tiempoMax, fueCritica, -1);
        }
    }

    private boolean backtrack(int tiempoMax, boolean[] fueCritica,int ultimaTareaCritica){
        estadosGenerados++;
        boolean solucionEncontrada = false;
        if(todasLasTareasAsignadas()){
            actualizarMejorSolucion();
            return true;
        }
        for(int i=0;i<procesadores.size();i++){
            Procesador procesador = procesadores.get(i);

            for(int j=0;j<tareas.size();j++) {
                Tarea tarea = tareas.get(j);
                if (tarea.estaAsignada() && puedeAsignarTarea(procesador, tarea, tiempoMax)) ;
                procesador.asignarTarea(tarea);
                boolean tareaEsCritica = tarea.isCritica();
                boolean[] fueCriticaNuevo = fueCritica.clone();
                fueCriticaNuevo[i] = tareaEsCritica;

                int nuevaTareaCritica = tareaEsCritica ? i : ultimaTareaCritica;


                if(tareaEsCritica && i == ultimaTareaCritica){
                    procesador.removerTarea(tarea);
                    continue;
                }
                if(backtrack(tiempoMax, fueCriticaNuevo, nuevaTareaCritica)){
                    solucionEncontrada = true;
                }
                procesador.removerTarea(tarea);



            }
        }
        return solucionEncontrada;

    }

    private boolean todasLasTareasAsignadas(){
        for(Tarea tarea : tareas){
            if(!tarea.estaAsignada()){
                return false;
            }
        }
        return true;
    }

    private boolean puedeAsignarTarea(Procesador procesador, Tarea tarea, int tiempoMax){
        int tiempoTotal = procesador.tiempoTotal() + tarea.getTiempo();
        if(tiempoTotal> tiempoMax && !procesador.isRefrigerado()){
            return false;
        }
        else{
            return true;
        }
    }

    private void actualizarMejorSolucion(){
        int tiempoMaximo = calcularTiempoMaximo();
        List<List<Tarea>> asignaciones = new ArrayList<>();
        for(Procesador procesador: procesadores){
            asignaciones.add(new ArrayList<>(procesador.getTareas()));
        }
        mejorSolucion = new Solucion(asignaciones, tiempoMaximo);
    }
    private int calcularTiempoMaximo(){
        int maxTiempo = 0;
        for(Procesador procesador: procesadores){
            maxTiempo = Math.max(maxTiempo, procesador.tiempoTotal());
        }
        return maxTiempo;
    }

    public void mostrarResultadoBacktracking(){
        if(mejorSolucion == null){
            System.out.println("No se encontró una solución valida.");

        }
        System.out.println("Solucion obtenida");
        List<List<Tarea>> asignaciones = mejorSolucion.getAsignaciones();
        for(int i=0;i<asignaciones.size();i++) {
            List<Tarea> asignacion = mejorSolucion.getAsignaciones().get(i);
            for(int j=0;j<asignacion.size();j++) {
                System.out.println("Procesador " + (i + 1) + " : " + asignacion.get(j));
            }

        }
        System.out.println("Tiempo maximo de ejecucion: " + mejorSolucion.getTiempoMaximo());
        System.out.println("Cantidad de estados generados: " + estadosGenerados);
        }

    public void greedy(int tiempoMax){
        if(tareas.isEmpty()){
            System.out.println("Proceso terminado");
        }

        candidatosConsiderados = 0;

        while(!todasLasTareasAsignadas()){
            Tarea mejorTarea = obtenerMejorTarea();
            if(mejorTarea == null){
                break;
            }
            Procesador mejorProc = obtenerMejorProcesador(tiempoMax, mejorTarea);
            if(mejorProc == null){
                break;
            }
            mejorProc.asignarTarea(mejorTarea);
        }
        actualizarMejorSolucion();
        mostrarResultadoGreedy();
    }

    private Tarea obtenerMejorTarea(){
        List<Tarea> lista = servicio.servicio2(true);
        if(!lista.isEmpty()){
            for(Tarea tarea: lista){
                if(!tarea.estaAsignada()){
                    return tarea;
                }
            }
        }
        else{
            lista = servicio.servicio2(false);
            for(Tarea tarea: lista){
                if(!tarea.estaAsignada()){
                    return tarea;
                }
            }
        }
        return null;
    }
    private Procesador obtenerMejorProcesador(int tiempoMax, Tarea tarea){
        Procesador mejorProc = null;
        int menorTiempoTotal = Integer.MAX_VALUE;

        for(Procesador procesador: procesadores){
            candidatosConsiderados++;
            int tiempoTotal = procesador.tiempoTotal() + tarea.getTiempo();
            if(tiempoTotal<=tiempoMax  ||  procesador.isRefrigerado()){
                if(tiempoTotal< menorTiempoTotal){
                    menorTiempoTotal = tiempoTotal;
                    mejorProc = procesador;
                }
            }
        }
        return mejorProc;
    }

    public void mostrarResultadoGreedy(){
        if(mejorSolucion == null){
            System.out.println("No se encontro solucion");
            return;
        }

        System.out.println("Solucion obtenida (Greedy): ");
        List<List<Tarea>> asignaciones = new ArrayList<>(mejorSolucion.getAsignaciones());
        for(int i=0;i<asignaciones.size();i++){
            System.out.println("Procesador " + (i + 1) + ": " + asignaciones.get(i));

        }
        System.out.println("Tiempo max de ejecucion: " + mejorSolucion.getTiempoMaximo());
        System.out.println("Cantidad de candidatos considerados: " + candidatosConsiderados);
    }
}
