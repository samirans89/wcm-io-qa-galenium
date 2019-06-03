/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.qa.galenium.imagecomparison;

import static io.wcm.qa.galenium.configuration.GaleniumConfiguration.getActualImagesDirectory;
import static io.wcm.qa.galenium.configuration.GaleniumConfiguration.getExpectedImagesDirectory;
import static io.wcm.qa.galenium.reporting.GaleniumReportUtil.getLogger;
import static io.wcm.qa.galenium.util.FileHandlingUtil.constructRelativePath;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.galenframework.specs.Spec;
import com.galenframework.validation.ImageComparison;
import com.galenframework.validation.ValidationError;
import com.galenframework.validation.ValidationResult;

import io.wcm.qa.galenium.exceptions.GaleniumException;
import io.wcm.qa.galenium.reporting.GaleniumReportUtil;
import io.wcm.qa.galenium.util.FileHandlingUtil;

/**
 * Utility methods for Galenium image comparison via Galen.
 */
final class IcUtil {

  private static final BufferedImage DUMMY_IMAGE = new BufferedImage(20, 20, BufferedImage.TYPE_3BYTE_BGR);

  private static final String REGEX_IMAGE_FILENAME = ".*image file ([^,]*\\.png).*";
  static final Pattern REGEX_PATTERN_IMAGE_FILENAME = Pattern.compile(REGEX_IMAGE_FILENAME);

  private IcUtil() {
    // do not instantiate
  }

  private static String getTargetPathFrom(Spec spec) {
    File rootDirectory = new File(getExpectedImagesDirectory());
    String imagePathFromSpec = getImagePathFrom(spec);
    String relativeImagePath = constructRelativePath(rootDirectory, new File(imagePathFromSpec));
    return getActualImagesDirectory() + File.separator + relativeImagePath;
  }

  private static File writeDummySample(File targetFile) {
    try {
      getLogger().trace("begin writing image '" + targetFile);
      FileHandlingUtil.ensureParent(targetFile);
      ImageIO.write(DUMMY_IMAGE, "png", targetFile);
      getLogger().trace("done writing image '" + targetFile);
      return targetFile;
    }
    catch (IOException ex) {
      throw new GaleniumException("could not write dummy image.", ex);
    }
  }

  static void createDummyIfSampleDoesNotExist(String fullFilePath) {
    if (IcUtil.isExpectedImageSampleMissing(fullFilePath)) {
      GaleniumReportUtil.getLogger().info("Cannot find sample. Substituting dummy for '" + fullFilePath + "'");

      // if image is missing, we'll substitute a dummy to force Galen to at least sample the page
      File targetFile = new File(fullFilePath);

      writeDummySample(targetFile);
    }
  }

  static File getActualSampleAsFileFrom(ValidationResult result) {
    File imageFile = getOriginalFilteredImage(result);
    if (imageFile == null) {
      imageFile = writeDummySample();
    }
    return imageFile;
  }

  static String getImageOrDummySamplePath(String folder, String fileName) {
    String fullFilePath;

    // folder
    if (StringUtils.isNotBlank(folder)) {
      fullFilePath = FilenameUtils.concat(folder, fileName);
    }
    else {
      // no folder means fileName is all the path info we have
      fullFilePath = fileName;
    }

    createDummyIfSampleDoesNotExist(fullFilePath);

    return fullFilePath;
  }

  static String getImagePathFrom(Spec spec) {
    Matcher matcher = REGEX_PATTERN_IMAGE_FILENAME.matcher(spec.toText());
    String imagePath = null;
    if (matcher.matches() && matcher.groupCount() >= 1) {
      imagePath = matcher.group(1);
    }
    return imagePath;
  }

  static File getOriginalFilteredImage(ValidationResult result) {
    ValidationError error = result.getError();
    String msg;
    if (error != null) {
      ImageComparison imageComparison = error.getImageComparison();
      if (imageComparison != null) {
        File actualImage = imageComparison.getOriginalFilteredImage();
        if (actualImage != null) {
          return actualImage;
        }
        else {
          msg = "could not find sampled image in image comparison.";
        }
      }
      else {
        msg = "could not find image comparison in validation error.";
      }
    }
    else {
      msg = "could not find error in validation result.";
    }
    getLogger().debug(msg);
    return null;
  }

  static File getSampleSourceFile(Spec spec, ValidationResult result) {
    String imagePath = getImagePathFrom(spec);
    File source = null;
    if (StringUtils.isNotBlank(imagePath)) {
      getLogger().debug("image: " + imagePath);
      source = getActualSampleAsFileFrom(result);
      if (source == null) {
        source = new File(imagePath);
      }
    }
    else {
      String msg = "could not extract image name from: " + spec.toText();
      getLogger().warn(msg);
    }
    return source;
  }

  static File getSampleTargetFile(Spec spec) {
    String targetPath = getTargetPathFrom(spec);
    File imageFile = new File(targetPath);
    FileHandlingUtil.ensureParent(imageFile);
    return imageFile;
  }

  static boolean isExpectedImageSampleMissing(String fullFilePath) {
    return !new File(fullFilePath).isFile();
  }

  static boolean isImageComparisonSpec(Spec spec) {
    return StringUtils.contains(spec.toText(), "image file ");
  }

  static void logSpec(Spec spec) {
    getLogger().debug("checking for image file: " + spec.toText() + " (with regex: " + REGEX_PATTERN_IMAGE_FILENAME.pattern() + ")");
  }

  static void saveSample(String objectName, Spec spec, ValidationResult result) {
    getLogger().trace("saving sample: " + objectName);
    logSpec(spec);
    File source = getSampleSourceFile(spec, result);
    if (source != null) {
      File target = getSampleTargetFile(spec);
      getLogger().trace("begin copying image '" + source + "' -> '" + target + "'");
      try {
        FileUtils.copyFile(source, target);
      }
      catch (GaleniumException | IOException ex) {
        String msg = "could not write image: " + target;
        getLogger().error(msg, ex);
      }
      getLogger().trace("done copying image '" + source + "' -> '" + target + "'");
    }
    else {
      getLogger().debug("did not find source file: " + objectName);
    }
  }

  static File writeDummySample() {
    try {
      File dummySample = File.createTempFile("dummy-sample", "png");
      return writeDummySample(dummySample);
    }
    catch (IOException ex) {
      throw new GaleniumException("could create temp file for dummy image.", ex);
    }
  }

}
