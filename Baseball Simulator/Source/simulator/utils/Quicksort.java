package simulator.utils;

import java.util.ArrayList;

import simulator.players.Player;

public class Quicksort {

	public static ArrayList<Player> quickSort(ArrayList<Player> list, int left, int right) {
		
		if (left >= right) {
            return list;
        }

        int pivot = right;
        int i = left;
        int j = right;

        while (i <= j) {
            while (list.get(i).sbAVG < list.get(pivot).sbAVG) {
                i++;
            }
            while (list.get(j).sbAVG > list.get(pivot).sbAVG) {
                j--;
            }
            if (i <= j) {
                Player node = list.get(i);
                list.set(i, list.get(j));
                list.set(j, node);
                i++;
                j--;
            }
        }
        if (left < j) {
            quickSort(list, left, j);
        }
        if (right > i) {
            quickSort(list, i, right);
        }
        return list;
	}
}
