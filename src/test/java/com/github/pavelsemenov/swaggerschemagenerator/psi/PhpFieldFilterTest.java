package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import junit.framework.TestCase;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PhpFieldFilterTest extends TestCase {
    PhpFieldFilter fieldFilter = new PhpFieldFilter();

    public void testBannedTypes() {
        PhpFieldFilter.BANNED_TYPES.forEach(t -> {
            assertThat(fieldFilter.isBanned(t)).isTrue();
            assertThat(fieldFilter.isBanned(PhpType.builder().add(t).build())).isTrue();
        });
    }

    public void testNotBannedTypes() {
        Arrays.asList(PhpType._INT, PhpType._STRING, PhpType._BOOL).forEach(t -> {
            assertThat(fieldFilter.isBanned(t)).isFalse();
            assertThat(fieldFilter.isBanned(PhpType.builder().add(t).build())).isFalse();
        });
    }

    public void testFirstTypeSingle() {
        PhpType type = PhpType.builder().add(PhpType._STRING).build();
        assertThat(fieldFilter.getFirstType(type)).isEqualTo(PhpType._STRING);
    }

    public void testFirstTypeNullable() {
        PhpType type = PhpType.builder().add(PhpType._INT).add(PhpType._NULL).build();
        assertThat(fieldFilter.getFirstType(type)).isEqualTo(PhpType._INT);
    }

    public void testFirstTypeMultiple() {
        PhpType type = PhpType.builder().add(PhpType._INT).add(PhpType._STRING).build();
        assertThat(fieldFilter.getFirstType(type)).isEqualTo(PhpType._NULL);
    }

    public void testFirstTypeMultipleNullable() {
        PhpType type = PhpType.builder().add(PhpType._INT).add(PhpType._STRING).add(PhpType._NULL).build();
        assertThat(fieldFilter.getFirstType(type)).isEqualTo(PhpType._NULL);
    }
}