package com.creditstxt.maven;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import org.apache.maven.model.Contributor;
import org.apache.maven.model.Developer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.Component;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Maven goal which produces the credits.txt file for a project.
 */
@Mojo(name = "creditstxt", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class CreditMojo extends AbstractMojo
{
    /**
     * Directory in which the credits.txt file will live.
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true,
        property = "creditstxt.outputDir")
    private File outputDir;

    /**
     * Name of the credits.txt file.
     */
    @Parameter(defaultValue = "credits.txt", required = true,
        property = "creditstxt.outputFile")
    private String outputFile;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Component
    private MavenProjectHelper helper;

    public void execute() throws MojoExecutionException
    {
        File oDir = this.outputDir;

        if (!oDir.exists())
        {
            oDir.mkdirs();
        }

        File creditsFile = new File(oDir, this.outputFile);

        PrintWriter pw = null;
        try
        {
            pw = new PrintWriter(creditsFile);

            this.generateCreditsText(pw);
            this.getLog().info("Wrote credits to " + creditsFile.toString());
        }
        catch ( IOException e )
        {
            throw
                new MojoExecutionException("Could not open " + creditsFile, e);
        }
        finally
        {
            if ( pw != null )
            {
                pw.close();
            }
        }

        this.helper
            .attachArtifact(this.project, "txt", "credits", creditsFile);
    }

    void generateCreditsText(PrintWriter pw) throws IOException
    {
        StringBuilder name = new StringBuilder();
        if (this.project.getName() == null || "".equals(this.project.getName()))
        {
            name.append(this.project.getGroupId());
            name.append(":");
            name.append(this.project.getArtifactId());
        }
        else
        {
            name.append(this.project.getName());
        }
        pw.write(name.toString());
        pw.write(" version ");
        pw.write(this.project.getVersion());
        pw.write("\n\n");

        if (this.project.getDescription() != null
            && !"".equals(this.project.getDescription()))
        {
            pw.write(this.project.getDescription());
            pw.write("\n\n");
        }

        if (this.project.getOrganization() != null)
        {
            pw.write("Organized by ");
            pw.write(this.project.getOrganization().getName());
            if (this.project.getOrganization().getUrl() != null)
            {
                pw.write(" ");
                pw.write(this.project.getOrganization().getUrl());
            }
            pw.write(".\n\n");
        }

        if (this.project.getDevelopers() != null
            && this.project.getDevelopers().size() > 0)
        {
            pw.write(name.toString());
            pw.write(" is developed by the following developers:\n\n");
            for (Developer dev : this.project.getDevelopers())
            {
                pw.write(dev.getName());
                if(dev.getRoles() != null && dev.getRoles().size() > 0)
                {
                    pw.write(" (");
                    boolean first = true;
                    for(String role : dev.getRoles())
                    {
                        if(!first) { pw.write(", "); }
                        else { first = false; }
                        pw.write(role);
                    }
                    pw.write(")");
                }
                if (dev.getOrganization() != null)
                {
                    pw.write(", ");
                    pw.write(dev.getOrganization());
                }
                if (dev.getEmail() != null)
                {
                    pw.write(" ");
                    pw.write(dev.getEmail());
                }
                if (dev.getUrl() != null)
                {
                    pw.write(" ");
                    pw.write(dev.getUrl());
                }

                pw.write("\n");
            }

            pw.write("\n");
        }

        if (this.project.getContributors() != null
            && this.project.getContributors().size() > 0)
        {
            pw.write(name.toString());
            pw.write(" has contributions from these contributors:\n\n");
            for (Contributor con : this.project.getContributors())
            {
                pw.write(con.getName());
                if(con.getRoles() != null && con.getRoles().size() > 0)
                {
                    pw.write(" (");
                    boolean first = true;
                    for(String role : con.getRoles())
                    {
                        if(!first) { pw.write(", "); }
                        else { first = false; }
                        pw.write(role);
                    }
                    pw.write(")");
                }
                if (con.getOrganization() != null)
                {
                    pw.write(", ");
                    pw.write(con.getOrganization());
                }
                if (con.getEmail() != null)
                {
                    pw.write(" ");
                    pw.write(con.getEmail());
                }
                if (con.getUrl() != null)
                {
                    pw.write(" ");
                    pw.write(con.getUrl());
                }

                pw.write("\n");
            }

            pw.write("\n");
        }
    }
}
