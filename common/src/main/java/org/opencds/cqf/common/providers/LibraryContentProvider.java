package org.opencds.cqf.common.providers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Function;

import org.cqframework.cql.cql2elm.FhirLibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;
import org.opencds.cqf.cql.evaluator.cql2elm.content.LibraryContentType;

import ca.uhn.fhir.cql.common.provider.LibraryResolutionProvider;

/**
 * Created by Christopher on 1/12/2017.
 */
public class LibraryContentProvider<LibraryType, AttachmentType>
        implements org.opencds.cqf.cql.evaluator.cql2elm.content.LibraryContentProvider {

    private FhirLibrarySourceProvider innerProvider;
    private LibraryResolutionProvider<LibraryType> provider;
    private Function<LibraryType, Iterable<AttachmentType>> getAttachments;
    private Function<AttachmentType, String> getContentType;
    private Function<AttachmentType, byte[]> getContent;

    public LibraryContentProvider(LibraryResolutionProvider<LibraryType> provider,
            Function<LibraryType, Iterable<AttachmentType>> getAttachments,
            Function<AttachmentType, String> getContentType, Function<AttachmentType, byte[]> getContent) {

        this.innerProvider = new FhirLibrarySourceProvider();

        this.provider = provider;
        this.getAttachments = getAttachments;
        this.getContentType = getContentType;
        this.getContent = getContent;
    }

    @Override
    public InputStream getLibrarySource(VersionedIdentifier versionedIdentifier) {
        try {
            LibraryType lib = this.provider.resolveLibraryByName(versionedIdentifier.getId(),
                    versionedIdentifier.getVersion());
            for (AttachmentType attachment : this.getAttachments.apply(lib)) {
                if (this.getContentType.apply(attachment).equals("text/cql")) {
                    return new ByteArrayInputStream(this.getContent.apply(attachment));
                }
            }
        } catch (Exception e) {
        }

        return this.innerProvider.getLibrarySource(versionedIdentifier);
    }

    @Override
    public InputStream getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType libraryContentType) {
 
        if (libraryContentType == LibraryContentType.CQL) {
            return this.getLibrarySource(libraryIdentifier);
        }

        return null;
    }
}