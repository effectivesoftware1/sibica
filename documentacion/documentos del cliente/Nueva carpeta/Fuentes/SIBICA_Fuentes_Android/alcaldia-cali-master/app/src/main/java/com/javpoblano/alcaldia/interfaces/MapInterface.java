package com.javpoblano.alcaldia.interfaces;

import com.javpoblano.alcaldia.models.InformacionPredio;
import com.javpoblano.alcaldia.models.PredioParse;

/**
 * Created by javpoblano on 12/24/16.
 */

public interface MapInterface {
    void onPolygonClick(PredioParse data);
}
