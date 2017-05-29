/*
 * (C) Copyright 2017-2017, by Joris Kinable and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.io;

import junit.framework.TestCase;
import org.jgrapht.Graph;
import org.jgrapht.graph.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Tests for Graph6Sparse6Importer
 *
 * @author Joris Kinable
 */
public class Graph6Sparse6ImporterTest extends TestCase{

    public <E> Graph<Integer, E> readGraph(
            InputStream in, Class<? extends E> edgeClass, boolean weighted)
            throws ImportException
    {
        Graph<Integer, E> g;
        if (weighted)
            g = new WeightedPseudograph<>(edgeClass);
        else
            g = new Pseudograph<>(edgeClass);

        Graph6Sparse6Importer<Integer, E> importer = new Graph6Sparse6Importer<>(
                (l, a) -> Integer.parseInt(l), (f, t, l, a) -> g.getEdgeFactory().createEdge(f, t));
        try {
            importer.importGraph(g, new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // cannot happen
        }

        return g;
    }

    @Test
    public void testExampleGraph()
            throws ImportException
    {
        String input = ":Fa@x^\n";

        Graph<Integer,
                DefaultEdge> graph = readGraph(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                DefaultEdge.class, false);

        assertEquals(7, graph.vertexSet().size());
        assertEquals(4, graph.edgeSet().size());

        int[][] edges = { {0,1}, {0,2}, {1,2}, {5,6} };
        for (int[] edge : edges)
            assertTrue(graph.containsEdge(edge[0], edge[1]));
    }

    @Test
    public void testNumberVertices1()
            throws ImportException
    {
        String input = "~??~?????_@?CG??B??@OG?C?G???GO??W@a???CO???OACC?OA?P@G??O??????G??C????c?G?CC?_?@???C_??_?C????PO?C_??AA?OOAHCA___?CC?A?CAOGO??????A??G?GR?C?_o`???g???A_C?OG??O?G_IA????_QO@EG???O??C?_?C@?G???@?_??AC?AO?a???O?????A?_Dw?H???__O@AAOAACd?_C??G?G@??GO?_???O@?_O??W??@P???AG??B?????G??GG???A??@?aC_G@A??O??_?A?????O@Z?_@M????GQ@_G@?C?\n";

        Graph<Integer,
                DefaultEdge> graph = readGraph(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                DefaultEdge.class, false);

        assertEquals(63, graph.vertexSet().size());
    }

    @Test
    public void testNumberVertices2()
            throws ImportException
    {
        String input = "_???C?@AA?_?A?O?C??S??O?q_?P?CHD??@?C?GC???C??GG?C_??O?COG????I?J??Q??O?_@@??@??????\n";

        Graph<Integer,
                DefaultEdge> graph = readGraph(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                DefaultEdge.class, false);

        assertEquals(32, graph.vertexSet().size());
    }

    @Test
    public void testSparse61()
            throws ImportException
    {
        //Klein7RegularGraph
        String input = ":W__@`AaBbC_CDbDcE`F_AG_@DEH_IgHIJbFGIKaFHILeFGHMdFKN_EKOPaCNPQ`HOQRcGLRS`BKMSTdJKLPTU\n";

        Graph<Integer,
                DefaultEdge> graph = readGraph(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                DefaultEdge.class, false);

        assertEquals(24, graph.vertexSet().size());
        assertEquals(84, graph.edgeSet().size());

        int[][] edges = { {0,1}, {0,2}, {1,2}, {5,6} };
        for (int[] edge : edges)
            assertTrue(graph.containsEdge(edge[0], edge[1]));
    }

    @Test
    public void testSparse62()
            throws ImportException
    {
        //TruncatedTetrahedralGraph
        String input = ":K`ESwC_EOyDl\\\\MCi\n";

        Graph<Integer,
                DefaultEdge> graph = readGraph(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                DefaultEdge.class, false);

        assertEquals(24, graph.vertexSet().size());
        assertEquals(84, graph.edgeSet().size());

        int[][] edges = { {0,1}, {0,2}, {1,2}, {5,6} };
        for (int[] edge : edges)
            assertTrue(graph.containsEdge(edge[0], edge[1]));
    }

    @Test
    public void testSparse63()
            throws ImportException
    {
        //HarborthGraph
        String input = ":s_OGKI?@_?g[QABAo__YEFCp@?iIEbqHWuWLbbh?}[OfcXpGhNHdYPY_SgdYX]pZkfJPuo[lfZHys^mFcDs}`pG{UNNgoHC}DIgrI[qjMhTyDQrQlVydrBYmWkn\n";

        Graph<Integer,
                DefaultEdge> graph = readGraph(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                DefaultEdge.class, false);

        assertEquals(24, graph.vertexSet().size());
        assertEquals(84, graph.edgeSet().size());

        int[][] edges = { {0,1}, {0,2}, {1,2}, {5,6} };
        for (int[] edge : edges)
            assertTrue(graph.containsEdge(edge[0], edge[1]));
    }

    @Test
    public void testSparse64()
            throws ImportException
    {
        //HallJankoGraph
        String input = (":~?@c__E@?g?A?w?A@GCA_?CA`OWF`W?EAW?@?_OD@_[GAgcIaGGB@OcIA"
        "wCE@o_K_?GB@?WGAouC@OsN_?GB@O[GB`A@@_e?@OgLB_{Q_?GC@O[GAOs"
        "OCWGBA?kKBPA@?_[KB_{OCPKT`o_RD`]A?o[HBOwODW?DA?cIB?wRDP[X`"
        "ogKB_{QD@]B@o_KBPWXE`mC@o_JB?{PDPq@?oWGA_{OCPKTDp_YEwCA@_c"
        "IBOwOC`OX_OGB@?WPDPcYFg?C@_gKBp?SE@cYF`{_`?SGAOoOC`_\\FwCE"
        "A?gKBO{QD@k[FqI??_OFA_oQE@k\\Fq?`GgCB@pGRD@_XFP{a_?SE@ocIA"
        "ooNCPOUEqU@?oODA?cJB_{UEqYC@_kLC@CREPk]GAGbHgCA@?SMBpCSD`["
        "YFq?`Ga]BA?gPC`KSD`_\\Fa?cHWGB@?[IAooPD`[WF@s^HASeIg?@@OcP"
        "C`KYF@w^GQ[h`O[HAooMC@CQCpSVEPk\\GaSeIG?FA?kLB_{OC`OVE@cYG"
        "QUA@?WLBp?PC`KVEqKgJg?DA?sMBpCSDP[WEQKfIay@?_KD@_[GC`SUE@k"
        "[FaKdHa[k_?OLC@CRD@WVEpo^HAWfIAciIqoo_?CB@?kMCpOUE`o\\GAKg"
        "IQgq_?GD@_[GB?{OCpWVE@cYFACaHAWhJR?q_?CC@_kKBpC\\GACdHa[kJ"
        "a{o_?CA?oOFBpGRD@o\\GaKdIQonKrOt_?WHA`?PC`KTD`k]FqSeIaolJr"
        "CqLWCA@OkKCPGRDpcYGAKdIAgjJAsmJr?t__OE@ogJB_{XEps`HA[gIQwn"
        "KWKGAOoMBpGUE`k[Fa?aHqckJbSuLw?@?_SHA_kLC@OTFPw^GaOkLg?B@?"
        "[HA_{PDP_XFaCbHa[gIqooKRWx_?CFBpOTE@cZFPw^GACcHQgoKrSvMwWG"
        "BOwQCp_YFP{`HASfJAwnKRSx_OSSDP[WEq?aGqSfIQsoKR_zNWCE@o_HA_"
        "sREPg^GAGcHQWfIAciKbOxNg?A@__IAooMC`KTD`g\\GAKcIasoKrOtLb["
        "wMbyCA?cKBp?TD`[WE`s^GQGbHqcjJrK{NRw~_oODA?sNC@CQCpOZF@s]G"
        "QOfIaolJrGsLbk}_?OFA_sRD@SVE`k[HQcjJa{qLb[xMb|?_OOFA?cIAos"
        "RDP_ZFa?aGqOfIAsuMbk{Ns@@OsQAA_sPDPWXE`o\\FqKdIQkkJrCuLr_x"
        "Mro}NsDAPG?@@OWFApKUE@o`IQolKRKsLrc|NsQC@OWGAOgJCpOWE`o_GQ"
        "KiIqwnKr_~OcLCPS]A?oWHA_oMBpKSDP[\\FagjKBWxMbk{OSQ@@O_IAoo"
        "LBpCSD`g\\FaGbHQWgIQgmKRKwMRl?PgGC@OWHB@KSE@c[FqCaGqSeIAkk"
        "KBCqLBSuMBpGQWCA@?cKBOwRDPWVE@k^GqOfJr?pKbKtLrs}OSHDQwKIBO"
        "wPD@WWEQ?`HQWfIQglKBOtLbo}Ns@@OsTE_?kLCpWWHA[gIqomKBGwMRgz"
        "NBw~OSPDPc\\H_?CFAOoLCPSVE`o\\GAOeJAwpKbKtMrx?Qcq??OKFA?gJ"
        "B`?QDpcYEpo]FqKfIAgjJB?qKr_{NS@A__SE@o_HBO{PC`OTD`{_HaciIq"
        "{vMbt?OcPFQCeB@?SKBOwRD@SXE`k[FPw`HQ_lKRKxNRxBPC\\HQclK_?K"
        "EB?sOC`OTDa?`GqWgJRCrNBw~OSHFQStMRtDQ_?KC@OoQE`k_GaOdHa[gI"
        "q{tMBg|Nb|?OcPMSDDQSwCB@_cJB_{OCpOVFP{dHa[jJQwqKrk}NsHBQCd"
        "MRtMA?oSEA_wPDp_YEpo]GAOeIq{pLBk}NsLEQCtNTDU??OKEA_oLC@[[G"
        "aKnKBOtLbk~OCPFQStNSDLSTgGKC@GSD`[WEpw_GQGcIAciJAwpKb_xMbk"
        "~QShJRc|R`_wNCPcZF@s^GAGbHA_hJR?qKrOvMRg|NsDEPsxTTgCB@?gJB"
        "?sMC@CUDp_]FqCaHQcjJQwtLrhCPS\\IRCtQTw?B@?SHA_wPC`_aGqOiJa"
        "{oKRKvMRpFQChKRtXVUTi??ocNC@KUE@cYFaGdHa_mJrKsLb[yMro|OcXI"
        "RdPTTddZaOgJB@?UEPk[FQCfIaolJrSvMBczNR|AOsXFQCtOTtaB@?WGAP"
        "?TEPo\\GAGdHqgmKBCqLR[xMb|?PC`HQs|TTt`XUtu@?o[HB?sNCPGXF@{"
        "_GQKcIqolJb_yNCLDPs`MRtDRTTdYUwSEA?kLB`CWF@s]FqGgIqooLRgzN"
        "RxFQSlMSDDQTDXVUTi@?_KDAOoLBpKUEQOfIa{oLB_xMrt?Os\\HQcpMST"
        "HSTtl[VT}A@ocJBOwSD`_XEpo_Ha_mJrKtLbgzNSTGQspLRtDUUDp\\WG["
        "HB`CQCp[WFQGgIQgkJQ{rLbc{Nc@APsdLRt@PSt\\WUtt_Wn")+"\n";

        Graph<Integer,
                DefaultEdge> graph = readGraph(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                DefaultEdge.class, false);

        assertEquals(24, graph.vertexSet().size());
        assertEquals(84, graph.edgeSet().size());

        int[][] edges = { {0,1}, {0,2}, {1,2}, {5,6} };
        for (int[] edge : edges)
            assertTrue(graph.containsEdge(edge[0], edge[1]));
    }

    @Test
    public void testGraph61()
            throws ImportException
    {
        //SchlaefliGraph
        String input = "ZBXzr|}^z~TTitjLth|dmkrmsl|if}TmbJMhrJX]YfFyTbmsseztKTvyhDvw\n";

        Graph<Integer,
                DefaultEdge> graph = readGraph(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                DefaultEdge.class, false);

        assertEquals(24, graph.vertexSet().size());
        assertEquals(84, graph.edgeSet().size());

        int[][] edges = { {0,1}, {0,2}, {1,2}, {5,6} };
        for (int[] edge : edges)
            assertTrue(graph.containsEdge(edge[0], edge[1]));
    }

    @Test
    public void testGraph62()
            throws ImportException
    {
        //GossetGraph
        String input = ('w~~~~rt{~Z\\ZxnvYZYmlfrb}|hDuhLlcmmMNf_^zzQGNYcP\\kcRZbaJjoNBx{'+
                '?N~o^}?A`}F_Kbbm_[QZ\\_]Cj\\oN_dm{BzB{?]WIMM@tPQRYBYRPIuAyJgQv?'+
                '|Bxb_M[kWIR@jTQcciDjShXCkFMgpwqBKxeKoS`TYqdTCcKtkdKwWQXrbEZ@OdU'+
                'mITZ@_e[{KXn?YPABzvY?IcO`zvYg@caC\\zlf?BaGR]zb{?@wOjv`~w??N_n_~'+
                '~w???^_^~~{')

        Graph<Integer,
                DefaultEdge> graph = readGraph(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                DefaultEdge.class, false);

        assertEquals(24, graph.vertexSet().size());
        assertEquals(84, graph.edgeSet().size());

        int[][] edges = { {0,1}, {0,2}, {1,2}, {5,6} };
        for (int[] edge : edges)
            assertTrue(graph.containsEdge(edge[0], edge[1]));
    }

}
