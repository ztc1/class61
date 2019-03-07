package com.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.sound.midi.Soundbank;
import java.io.File;

public class IndexManager {

//    1、创建索引  IndexWriter
    @Test
    public void testWriter() throws Exception{
//        准备一个存储索引文件的路径
        FSDirectory directory = FSDirectory.open(new File("D:\\class61\\index_repo"));
//        Version matchVersion, Analyzer analyzer
//        指定使用哪种分词器  标准分词器
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST,analyzer);
        //    创建IndexWriter对象
        IndexWriter indexWriter = new IndexWriter(directory,conf);

        indexWriter.deleteAll(); //删除所有索引

        File filePaths = new File("D:\\ITCAST\\Lucene&git\\Lucene\\资料\\上课用的查询资料searchsource");
        File[] files = filePaths.listFiles();
        for (File file : files) {
            Document document = new Document();
//        p1: 域的名称  p2: 值  p3:是否存储原内容 create web page.txt
            String fileName = file.getName();
            document.add(new TextField("filename",fileName, Field.Store.YES));

            String fileContent = FileUtils.readFileToString(file, "utf-8");
            document.add(new TextField("filecontent",fileContent, Field.Store.YES));
            String filePath = file.getPath();
            document.add(new StringField("filepath",filePath, Field.Store.YES));
            Long fileSize = FileUtils.sizeOf(file);
            document.add(new LongField("filesize", fileSize, Field.Store.YES));

            indexWriter.addDocument(document);
        }
        indexWriter.close();

    }


//    2、从索引中查询
    @Test
    public void  indexReader() throws Exception{

        Directory directory = FSDirectory.open(new File("D:\\class61\\index_repo"));
//        创建读取文档的对象
        IndexReader indexReader = DirectoryReader.open(directory);
//        创建查询的对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        
//        按照term查询  域名：值 查询标题带apache的
        Query query = new TermQuery(new Term("filename","全文检索.txt"));
//        Query query = new TermQuery(new Term("filename","全文"));
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;

        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document document = indexSearcher.doc(docID);
            System.out.println("标题："+document.get("filename"));
//            System.out.println("内容："+document.get("filecontent"));
            System.out.println("路径："+document.get("filepath"));
            System.out.println("大小："+document.get("filesize"));
            System.out.println("-----------------------------");
        }

        indexReader.close();
    }


    @Test
    public void testAnalyzer() throws Exception{
//        Analyzer analyzer = new StandardAnalyzer();
//        Analyzer analyzer = new CJKAnalyzer();
//        Analyzer analyzer = new SmartChineseAnalyzer();
        Analyzer analyzer = new IKAnalyzer();

//        TokenStream tokenStream = analyzer.tokenStream("test", "The Spring Framework provides a comprehensive programming and configuration model.");
        TokenStream tokenStream = analyzer.tokenStream("test", "使用indexwriter对象将他妈的document对象写入索引库，此过程是进行索引的创建，这句话是传智播客的白面郎君说的");
//        设置引用，为了获取每个分词的结果
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset(); //指针位置归0
        while (tokenStream.incrementToken()){
            System.out.println(charTermAttribute);
        }

    }


}
