package tpe.utils;

import java.util.Comparator;

public class ComparadorSolucion implements Comparator<Solucion> {
    @Override
    public int compare(Solucion o1, Solucion o2) {
        return o1.getTiempoMaximo() - o2.getTiempoMaximo();
    }
}
