/*******************************************************************************
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package org.jboss.portletbridge.tools.simpleConverter.convertor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Converter for project's pom.xml file add PortletBridge dependency,
 * and create property for PBR's version for easier updating in future.
 * 
 * @author <a href="mailto:jjamrich@redhat.com">Jan Jamrich</a>
 * @version $Revision$
 */
public class POMXmlConvertor extends SimpleJDOMConvertor {
	
	public static final String PROPERTIES = "properties";
	public static final String DEPENDENCIES = "dependencies";
	public static final String REPOSITORIES = "repositories";
	public static final String DEPENDENCY = "dependency";
	public static final String REPOSITORY = "repository";
	public static final String URL = "url";
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String ARTIFACT_ID = "artifactId";
	public static final String GROUP_ID = "groupId";
	public static final String VERSION = "version";
	public static final String PBR_VERSION = "portletbridge.version";
	public static final String EXCLUSIONS = "exclusions";
	public static final String EXCLUSION = "exclusion";
	
	public static final String PBR_IMPL = "portletbridge-impl";
	public static final String PBR_API = "portletbridge-api";
	public static final String PBR_GROUP_ID = "org.jboss.portletbridge";
	public static final String PBR_VERSION_EL = "${portletbridge.version}";
	
	public static final String JAVAX_FACES = "javax.faces";
	public static final String JSF_IMPL = "jsf-impl";
	public static final String JSF_API = "jsf-api";
	
	public static final MavenRepoBean[] MAVEN_REPOS = {
		new MavenRepoBean("jboss-maven2", "JBoss maven2 public repo", 
				"http://repository.jboss.org/maven2"),
		new MavenRepoBean("jboss-public-nexus-repository", 
				"JBoss Public Maven Repository Group",
				"https://repository.jboss.org/nexus/content/groups/public/")};
	
	private String pbVersion;

	public POMXmlConvertor(File xmlFile, String pbVersion) throws JDOMException, IOException {
		super(xmlFile);
		this.pbVersion = pbVersion;
	}

	/**
	 * This transformation adds portletbridge dependencies into pom.xml
	 */
	@Override
	public void transform() {
		Element dependencies = rootNode.getChild(DEPENDENCIES, ns);
		Element properties = rootNode.getChild(PROPERTIES, ns);
		Element repositories = rootNode.getChild(REPOSITORIES, ns);
		
		if (properties==null) {
			rootNode.addContent(new Element(PROPERTIES, ns));
		}
		properties.addContent(new Element(PBR_VERSION, ns).setText(pbVersion));
		
		if (repositories==null){
			rootNode.addContent(new Element(REPOSITORIES, ns));
		}
		addRepositories(repositories);
		
		dependencies.addContent(createPbrDepElem(PBR_API));
		dependencies.addContent(createPbrDepElem(PBR_IMPL));
		
		saveDocument();		
	}
	
	/**
	 * Create 'dependency' element, 
	 *  with artifactId element value given by <b>pbArtifactId</b> param 
	 * @param pbArtifactId
	 * @return Element &lt;dependency&gt; with appropriate child elements initialized
	 */
	protected Element createPbrDepElem(String pbArtifactId) {
		
		Element dependency = new Element(DEPENDENCY, ns);
		
		Element groupId = new Element(GROUP_ID, ns).setText(PBR_GROUP_ID);
		Element artifactId = new Element(ARTIFACT_ID, ns).setText(pbArtifactId);
		Element version = new Element(VERSION, ns).setText(PBR_VERSION_EL);
		
		dependency.addContent(groupId)
			.addContent(artifactId)
			.addContent(version)
			.addContent(createPbrDepExclusions());
		
		return dependency;
	}
	
	/**
	 * Create 'exclusions' element.
	 *  PBR depends on <b>jsf-api</b> and <b>jsf-impl</b>, 
	 *   which converted project usually contains before.
	 * @return Element &lt;exclusions&gt; with appropriate child elements initialized
	 */
	protected Element createPbrDepExclusions(){
		// containing exclusions element
		Element exclusions = new Element(EXCLUSIONS, ns);
		
		// first exclusion content
		Element groupId = new Element(GROUP_ID, ns).setText(JAVAX_FACES);
		Element artifactId = new Element(ARTIFACT_ID, ns).setText(JSF_API);
		
		// first exclusion
		Element exclusion1 = new Element(EXCLUSION, ns);
		exclusion1.addContent(groupId).addContent(artifactId);
		
		// second exclusion containing different artifactId
		Element exclusion2 = new Element(EXCLUSION, ns);
		artifactId = new Element(ARTIFACT_ID, ns).setText(JSF_IMPL);
		// I need the same groupId, but need create new instance of this object
		exclusion2.addContent((Element)groupId.clone()).addContent(artifactId);
		
		// set exclusions created above to exclusions elem
		exclusions.addContent(exclusion1).addContent(exclusion2);
		
		return exclusions;
	}
	
	/**
	 * Create 'exclusions' element.
	 *  PBR depends on <b>jsf-api</b> and <b>jsf-impl</b>, 
	 *   which converted project usually contains before.
	 * @return Element &lt;exclusions&gt; with appropriate child elements initialized
	 */
	protected Element createMavenRepoElem(MavenRepoBean mavenRepo){
		
		Element repository = new Element(REPOSITORY, ns);
		
		repository.addContent( new Element(ID, ns).setText(mavenRepo.getId()) );
		repository.addContent( new Element(NAME, ns).setText(mavenRepo.getName()) );
		repository.addContent( new Element(URL, ns).setText(mavenRepo.getUrl()) );
		
		return repository;
	}
	
	/**
	 * Check content of repositories element (given as argument),
	 * and add required 'repository' elements if are missing.
	 * If found required repository (by URL), leave ('repository') element untouched
	 * 
	 * @param Element repositories
	 */
	protected void addRepositories(Element repositories) {
		
		@SuppressWarnings("unchecked")
		Iterator<Element> repositoryIt = repositories.getChildren(REPOSITORY, ns).iterator();
		
		for (int x=0; x < MAVEN_REPOS.length; ++x) {
			boolean repoUrlFound = false;
			while (repositoryIt.hasNext()) {
				Element repository = repositoryIt.next();
				
				System.out.println(" Found maven repo with id: " + repository.getChildText(ID, ns) 
						+ ", url: " + repository.getChildText(URL, ns));
				
				if (MAVEN_REPOS[x].getUrl().equals(repository.getChildText(URL, ns))){
					repoUrlFound = true;
					break;
				}			
			}
			if (!repoUrlFound) {
				repositories.addContent(createMavenRepoElem(MAVEN_REPOS[x]));
			}
		}		
	}
	
}

/**
 * Bean with properties of maven repo definition in pom.xml
 *
 */
class MavenRepoBean {
	private String id;
	private String name;
	private String url;
	
	public MavenRepoBean(String id, String name, String url){
		this.id = id;
		this.name = name;
		this.url = url;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
