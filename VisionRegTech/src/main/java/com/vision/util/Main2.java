package com.vision.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.FileSystemUtils;

public class Main2 {
	public static void main(String[]args) throws IOException {
		String uploadDir = "/C:/Vision/NON_ADF_XL_Upload_Dir/";
		String adUploadDir = "/C:/Vision/NON_ADF_XL_AD_Dir/";

		String uploadDirNew = "";
		if(uploadDir.indexOf("/") == 0) {
			String regex = "\\";
			uploadDirNew = uploadDir.replaceAll("//", regex);
			uploadDirNew = uploadDirNew.substring(1, uploadDirNew.length());
		}else {
			uploadDirNew = uploadDir;
		}
		String uploadAdDirNew = "";
		if(adUploadDir.indexOf("/") == 0) {
			uploadAdDirNew = adUploadDir.replaceAll("//", "\\");
			uploadAdDirNew = uploadAdDirNew.substring(1, uploadAdDirNew.length());
		}else {
			uploadAdDirNew = adUploadDir;
		}
		String fileName = "Move Tile.txt";
		String version = "3";
		String fileExtension  ="txt";
		String adFile = "Bala_Satya_Prakash_"+version+"."+fileExtension;
		System.out.println("######################################");
		System.out.println("Upload Dir : "+uploadDirNew+" File : "+fileName);
		System.out.println("Upload AD Dir : "+uploadAdDirNew+" AD File : "+adFile);
		File srcFile = new File(uploadDirNew+""+fileName);
		File dstDir = new File(uploadAdDirNew+""+adFile);
//		FileUtils.copyFileToDirectory(srcFile, dstDir);
		try {
//			File sourceHTMLFiles = new File("C:\\HTML Files");
			FileSystemUtils.copyRecursively(srcFile, dstDir);
//			FileUtils.copyFileToDirectory(srcFile, dstDir);
			} catch (IOException e) {
//	            e.printStackTrace();
	        }
		System.out.println("File Moved to AD Dir Successfully!! ");
		System.out.println("######################################");
	
		
		/*List<SampleVb> originalList = new ArrayList<>();
		SampleVb sampleVb = new SampleVb();
		sampleVb.setParentId(1);
		sampleVb.setId(1);
		originalList.add(sampleVb);
		
		sampleVb = new SampleVb();
		sampleVb.setParentId(1);
		sampleVb.setId(2);
		originalList.add(sampleVb);
		
		sampleVb = new SampleVb();
		sampleVb.setParentId(1);
		sampleVb.setId(3);
		originalList.add(sampleVb);
		
		sampleVb = new SampleVb();
		sampleVb.setParentId(1);
		sampleVb.setId(4);
		originalList.add(sampleVb);
		
		sampleVb = new SampleVb();
		sampleVb.setParentId(4);
		sampleVb.setId(5);
		originalList.add(sampleVb);
		
		sampleVb = new SampleVb();
		sampleVb.setParentId(4);
		sampleVb.setId(6);
		originalList.add(sampleVb);
		Main2 main2 = new Main2();
		List<SampleVb> output = main2.getHierarchicalList(originalList);
		System.out.println(output.size());
		System.out.println(output.get(0).getChilds().size());*/
	}
	public List<SampleVb> getHierarchicalList(final List<SampleVb> originalList) {
	    final List<SampleVb> copyList = new ArrayList<>(originalList);

	    copyList.forEach(element -> {
	        originalList
	                .stream()
	                .filter(parent -> parent.id == element.parentId && parent.id != element.id)
	                .findAny()
	                .ifPresent(parent -> {
	                    if (parent.childs == null) {
	                        parent.childs = new ArrayList<>();
	                    }
	                    parent.childs.add(element);
	                    originalList.remove(element);
	                });
	    });
	    return originalList;
	}
	
	
	
}

