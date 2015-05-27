/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import crossLinker.CrossLinker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Sule
 */
public abstract class CrossLinkedPeptides {

    protected CrossLinker linker;
    protected FragmentationMode fragmentation_mode;
    protected HashSet<CPeptideIon> theoretical_ions = new HashSet<CPeptideIon>();
    protected IonFactory fragmentFactory = IonFactory.getInstance();
    protected boolean is_monoisotopic_mass = true,
            is_Branching, // true/false to create linkedPeptideFragmentIons with Branching/Attaching
            isMassCalculated = false;
    protected double intensity = 100,
            theoretical_xlinked_mass = 0;
    protected CrossLinkingType linkingType;

    public CrossLinker getLinker() {
        return linker;
    }

    public FragmentationMode getFragmentation_mode() {
        return fragmentation_mode;
    }

    public HashSet<CPeptideIon> getTheoretical_ions() {
        return theoretical_ions;
    }

    public IonFactory getFragmentFactory() {
        return fragmentFactory;
    }

    public boolean isIs_monoisotopic_mass() {
        return is_monoisotopic_mass;
    }

    public boolean isIs_Branching() {
        return is_Branching;
    }

    public boolean isIsMassCalculated() {
        return isMassCalculated;
    }

    public double getIntensity() {
        return intensity;
    }

    public double getTheoretical_xlinked_mass() {
        return theoretical_xlinked_mass;
    }

    public CrossLinkingType getLinkingType() {
        return linkingType;
    }

    /**
     * This method retrieves product ions in a selected mode for only a peptide
     *
     * @param ion_type
     * @param linked_index
     * @param product_ions
     * @param mass_shift
     * @param pepName
     * @param cPepIonType
     *
     * @return NI IONS!
     */
    public HashSet<CPeptideIon> prepareBackbone(HashMap<Integer, ArrayList<Ion>> product_ions,
            int ion_type, int linked_index, double mass_shift, String pepName, CPeptideIonType cPepIonType) {
        HashSet<CPeptideIon> backbones = new HashSet<CPeptideIon>();
        String abbrIonType = LinkedPeptideFragmentIon.getAbbrIonType(ion_type);
        String rootName = pepName + "_" + abbrIonType;
        ArrayList<Ion> tmp_ions = product_ions.get(ion_type);
        for (int index = 0; index < tmp_ions.size(); index++) {
            Ion ion = tmp_ions.get(index);
            double ion_mass = ion.getTheoreticMass();
            if (index > linked_index && linkingType.equals(CrossLinkingType.CROSSLINK)) { // from a linker index on a peptide, shift remaining ions with a mass of a linkedPeptide       
                ion_mass += mass_shift + linker.getMassShift_Type2();
            } else if (index >= linked_index && linkingType.equals(CrossLinkingType.MONOLINK)) {
                ion_mass += mass_shift;
            }
            int index_to_show = index + 1;
            String ionName = rootName + index_to_show;
            boolean isFound = false;
            // check if there is an ion with the same mass already...Because there are two N-terminis and C-terminis!
            for (CPeptideIon cPepTheo : theoretical_ions) {
                double tmp_cpeptheo = cPepTheo.getMass();
                if (Math.abs(tmp_cpeptheo - ion_mass) < 0.0000001) {
                    ionName = cPepTheo.getName() + "_" + ionName;
                    cPepTheo.setName(ionName);
                    isFound = true;
                }
            }
            if (!isFound) {
                CPeptideIon cIon = new CPeptideIon(intensity, ion_mass, cPepIonType, ion_type, ionName);
                backbones.add(cIon);
                theoretical_ions.add(cIon);
            }
        }
        return backbones;
    }

}
